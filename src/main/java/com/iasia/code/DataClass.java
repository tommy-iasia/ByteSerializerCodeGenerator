package com.iasia.code;

import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class DataClass {

    private DataClass(String className, String filePath, List<DataProperty> properties) {
        this.className = className;
        this.filePath = filePath;
        this.properties = properties;
    }

    public final String filePath;
    private String getOutputPath() {
        return filePath.replaceAll("\\.yaml$", ".java");
    }
    private String getPackage() {
        return filePath
                .replace('\\', '.')
                .replaceAll("\\.[^.]+.yaml$", "")
                .replaceAll(".+\\.src\\.(main\\.)?(java\\.)?", "");
    }

    public final String className;
    public final List<DataProperty> properties;

    @SuppressWarnings("unchecked")
    private static DataClass read(String filePath) throws IOException {
        try (var fileReader = new FileReader(filePath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> fileMap = yaml.load(fileReader);
            var fileEntry = fileMap.entrySet().iterator().next();

            var className = fileEntry.getKey();

            var propertiesList = new ArrayList<DataProperty>();
            var dataClass = new DataClass(className, filePath, propertiesList);

            var propertiesArray = (ArrayList<Object>) fileEntry.getValue();
            for (var propertyValue : propertiesArray) {
                var property = DataProperty.parse(propertyValue, dataClass);
                propertiesList.add(property);
            }

            return dataClass;
        }
    }

    public static void write(String filePath) throws IOException {
        var dataClass = read(filePath);
        var lines = dataClass.lines();

        var outputPath = dataClass.getOutputPath();
        try (var fileWriter = new FileWriter(outputPath);
             var printWriter = new PrintWriter(fileWriter)) {
            for (var line : lines) {
                printWriter.println(line);
            }
        }
    }

    private List<String> lines() {
        var lines = new LinkedList<String>();

        var classPackage = getPackage();
        lines.add("package " + classPackage + ";");
        lines.add("");

        lines.add("import java.nio.ByteBuffer;");
        lines.add("import java.nio.ByteOrder;");
        lines.add("");

        lines.add("public class " + className + " {");

        lines.add("");

        var constructor = constructor();
        addLines(lines, constructor, "    ");

        var fields = fields();
        if (!fields.isEmpty()) {
            lines.add("");
            addLines(lines, fields, "    ");
        }

        var getters = getters();
        if (!getters.isEmpty()) {
            addParts(lines, getters, "    ");
        }

        var setters = setters();
        if (!setters.isEmpty()) {
            addParts(lines, setters, "    ");
        }

        lines.add("");

        var read = read();
        addLines(lines, read, "    ");

        lines.add("");

        var write = write();
        addLines(lines, write, "    ");

        var length = length();
        addLines(lines, length, "    ");

        var fill = fill();
        addLines(lines, fill, "    ");

        lines.add("}");

        return lines;
    }
    private static void addParts(List<String> lines, List<List<String>> parts, String indent) {
        for (var part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            lines.add("");

            for (var line : part) {
                lines.add(indent + line);
            }
        }
    }
    private static void addLines(List<String> lines, List<String> adds, String indent) {
        for (var line : adds) {
            lines.add(indent + line);
        }
    }

    private List<String> constructor() {
        var lines = new LinkedList<String>();

        var fullProperties = properties.stream()
                .filter(t -> t.name != null)
                .collect(Collectors.toList());

        var requiredProperties = fullProperties.stream()
                .filter(DataProperty::constructorRequired)
                .collect(Collectors.toList());

        if (requiredProperties.size() < fullProperties.size()) {
            var requiredConstructor = constructor(requiredProperties);
            lines.addAll(requiredConstructor);
        }

        var fullConstructor = constructor(fullProperties);
        lines.addAll(fullConstructor);

        return lines;
    }
    private List<String> constructor(List<DataProperty> properties) {
        if (properties.isEmpty()) {
            return Collections.singletonList("public " + className + "() { }");
        } else {
            var lines = new LinkedList<String>();

            var parameters = properties.stream()
                    .map(DataProperty::constructorParameter)
                    .collect(Collectors.toList());

            lines.add("public " + className + "(" + String.join(", ", parameters) + ") {");

            for (var property : properties) {
                var set = property.innerSet();
                if (set != null) {
                    lines.add("    " + set);
                }
            }

            lines.add("}");

            return lines;
        }
    }

    private List<String> fields() {
        return properties.stream()
                .map(DataProperty::field)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<List<String>> getters() {
        return properties.stream()
                .map(DataProperty::getter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private List<List<String>> setters() {
        return properties.stream()
                .map(DataProperty::setter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> read() {
        var lines = new LinkedList<String>();

        lines.add("public static " + className + " get(ByteBuffer buffer) {");

        for (var property : properties) {
            var read = property.read();
            addLines(lines, read, "    ");
        }

        lines.add("");

        var names = properties.stream()
                .map(t -> t.name)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        lines.add("    return new " + className + "(" + String.join(", ", names) + ");");

        lines.add("}");

        return lines;
    }

    private List<String> write() {
        return Arrays.asList(
                "public ByteBuffer put() {",
                "    var length = length();",
                "    var buffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);",
                "",
                "    put(buffer);",
                "",
                "    return buffer;",
                "}");
    }
    private List<String> length() {
        var lines = new LinkedList<String>();

        lines.add("public int length() {");

        if (properties.isEmpty()) {
            lines.add("    return 0;");
        } else {
            var lengths = properties.stream().map(DataProperty::length).collect(Collectors.toList());
            lines.add("    return " + String.join(" + ", lengths) + ";");
        }

        lines.add("}");

        return lines;
    }
    private List<String> fill() {
        var lines = new LinkedList<String>();

        lines.add("public void put(ByteBuffer buffer) {");

        for (var property : properties) {
            var fill = property.fill();
            addLines(lines, fill, "    ");
        }

        lines.add("}");

        return lines;
    }
}
