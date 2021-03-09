package com.iasia.code;

import com.iasia.buffer.Ascii8;

import java.util.*;

public class DataProperty {

    private DataProperty(String name, List<Object> tokens, DataClass dataClass) {
        this.name = name;
        this.tokens = tokens;
        this.dataClass = dataClass;
    }

    public final String name;
    private String upperName() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public final List<Object> tokens;
    private Integer findNumber() {
        for (Object token : tokens) {
            if (token instanceof Integer) {
                return (Integer) token;
            } else if (token instanceof String) {
                try { return Integer.parseInt((String) token); }
                catch (NumberFormatException ignored) { }
            }
        }

        return null;
    }
    @SuppressWarnings("unchecked")
    private DataEnum findEnum() {
        var valuesOptional = tokens.stream()
                .filter(t -> t instanceof HashMap)
                .map(t -> ((HashMap<String, Object>) t).get("enum"))
                .filter(t -> t instanceof ArrayList)
                .map(t -> (ArrayList<String>) t)
                .findFirst();

        if (valuesOptional.isEmpty()) {
            return null;
        }

        var values = valuesOptional.get();
        if (values.size() < 4) {
            throw new RuntimeException();
        }

        var packageName = values.get(0);
        var className = values.get(1);

        var parser = values.get(2);
        var valuer = values.get(3);

        return new DataEnum(packageName, className, parser, valuer);
    }

    private final DataClass dataClass;

    @SuppressWarnings("unchecked")
    static DataProperty parse(Object value, DataClass dataClass) {
        if (value instanceof Map) {
            var propertyMap = (Map<String, Object>) value;
            var propertyEntry = propertyMap.entrySet().iterator().next();

            var propertyName = propertyEntry.getKey();
            var propertyValue = propertyEntry.getValue();
            return parse(propertyName, propertyValue, dataClass);
        } else {
            return parse(null, value, dataClass);
        }
    }
    @SuppressWarnings("unchecked")
    private static DataProperty parse(String name, Object value, DataClass dataClass) {
        if (value instanceof Integer) {
            var tokens = Collections.singletonList(value);
            return new DataProperty(name, tokens, dataClass);
        } else if (value instanceof String) {
            var texts = ((String) value).split("\\s+");
            var tokens = new ArrayList<Object>(Arrays.asList(texts));
            return new DataProperty(name, tokens, dataClass);
        } else if (value instanceof ArrayList) {
            var tokens = (ArrayList<Object>) value;
            return new DataProperty(name, tokens, dataClass);
        } else {
            throw new RuntimeException();
        }
    }

    public List<String> imports() {
        if (tokens.contains("Ascii8")) {
            return Collections.singletonList(Ascii8.class.getCanonicalName());
        } else {
            var enumClass = findEnum();
            if (enumClass != null) {
                var classPackage = dataClass.getPackage();
                if (enumClass.packageName.equals(classPackage)) {
                    return Collections.emptyList();
                } else {
                    return Collections.singletonList(enumClass.packageName + "." + enumClass.className);
                }
            } else {
                return Collections.emptyList();
            }
        }
    }

    public boolean constructorRequired() {
        if (name == null) {
            return false;
        }

        return tokens.contains("final");
    }
    public String constructorParameter() {
        return valueClass() + " " + name;
    }

    public String field() {
        if (name == null) {
            return null;
        }

        return valueScope()
                + (tokens.contains("final") ? " final " : " ")
                + valueClass() + " "
                + name + ";";
    }
    private String valueScope() {
        if (tokens.contains("protected")) {
            return "protected";
        } else if (tokens.contains("getter")
                && tokens.contains("setter")) {
            return "private";
        } else {
            return "public";
        }
    }
    private String valueClass() {
        var enumClass = findEnum();
        if (enumClass != null) {
            return enumClass.className;
        } else {
            if (tokens.contains("short")) {
                return tokens.contains("unsigned") ?
                        "int" : "short";
            } else if (tokens.contains("int")) {
                return tokens.contains("unsigned") ?
                        "long" : "int";
            } else if (tokens.contains("long")) {
                return "long";
            } else if (tokens.contains("Ascii8")) {
                return "String";
            } else {
                var number = findNumber();
                if (number != null) {
                    return number == 1 ?
                            "byte" : "byte[]";
                } else {
                    return tokens.contains("unsigned") ?
                            "int" : "byte";
                }
            }
        }
    }

    public List<String> getter() {
        if (!tokens.contains("getter")) {
            return null;
        }

        if (tokens.contains("fluent")) {
            return Arrays.asList(
                    "public " + valueClass() + " " + name + "() {",
                    "    return " + name + ";",
                    "}");
        } else {
            return Arrays.asList(
                    "public " + valueClass() + " get" + upperName() + "() {",
                    "    return " + name + ";",
                    "}");
        }
    }

    public List<String> setter() {
        if (!tokens.contains("setter")) {
            return null;
        }

        if (tokens.contains("final")) {
            return null;
        }

        if (tokens.contains("fluent")) {
            return Arrays.asList(
                    "public " + dataClass.className + " " + name + "(" + valueClass() + " " + name + ") {",
                    "    " + innerSet(),
                    "    return this;",
                    "}");
        } else {
            return Arrays.asList(
                    "public void set" + upperName() + "(" + valueClass() + " " + name + ") {",
                    "    " + innerSet(),
                    "}");
        }
    }
    public String innerSet() {
        return "this." + name + " = " + name + ";";
    }

    public List<String> read() {
        if (tokens.contains("exclude")) {
            return Collections.emptyList();
        }

        var readValue = readValue();
        if (name != null) {
            var enumClass = findEnum();
            if (enumClass != null) {
                return Collections.singletonList(
                        "var " + name + " = " + enumClass.className + "." + enumClass.parser + "(" + readValue + ");");
            } else {
                return Collections.singletonList("var " + name + " = " + readValue + ";");
            }
        } else {
            return Collections.singletonList(readValue + ";");
        }
    }
    private String readValue() {
        if (tokens.contains("short")) {
            if (tokens.contains("unsigned")) {
                return "Short.toUnsignedInt(buffer.getShort())";
            } else {
                return "buffer.getShort()";
            }
        } else if (tokens.contains("int")) {
            if (tokens.contains("unsigned")) {
                return "Integer.toUnsignedLong(buffer.getInt())";
            } else {
                return "buffer.getInt()";
            }
        } else if (tokens.contains("long")) {
            return "buffer.getLong()";
        } else if (tokens.contains("Ascii8")) {
            var number = findNumber();
            if (number != null) {
                return "Ascii8.getString(buffer, " + number + ")"
                        + (tokens.contains("trim") ? ".trim()" : "");
            } else {
                return "Ascii8.getString(buffer)";
            }
        } else {
            var number = findNumber();
            if (number != null) {
                if (number == 1) {
                    return "buffer.get()";
                } else {
                    return "ByteSerializable.getBytes(buffer, " + number + ")";
                }
            } else {
                if (tokens.contains("unsigned")) {
                    return "Byte.toUnsignedInt(buffer.get())";
                } else {
                    return "buffer.get()";
                }
            }
        }
    }

    public String length() {
        if (tokens.contains("exclude")) {
            return null;
        }

        if (tokens.contains("short")) {
            return "2";
        } else if (tokens.contains("int")) {
            return "4";
        } else if (tokens.contains("long")) {
            return "8";
        } else if (tokens.contains("Ascii8")) {
            var number = findNumber();
            return number != null ? number.toString() : name + ".length()";
        } else {
            var number = findNumber();
            return number != null ? number.toString() : "1";
        }
    }

    public List<String> fill() {
        if (tokens.contains("exclude")) {
            return Collections.emptyList();
        }

        var enumClass = findEnum();

        if (tokens.contains("short")) {
            if (name != null) {
                return Collections.singletonList(
                        "buffer.putShort("
                                + (tokens.contains("unsigned") ? "(short) " : "")
                                + name
                                + (enumClass != null ? "." + enumClass.valuer : "")
                                + ");");
            } else {
                return Collections.singletonList("buffer.putShort((short) 77);");
            }
        } else if (tokens.contains("int")) {
            if (name != null) {
                return Collections.singletonList(
                        "buffer.putInt("
                                + (tokens.contains("unsigned") ? "(int) " : "")
                                + name
                                + (enumClass != null ? "." + enumClass.valuer : "")
                                + ");");
            } else {
                return Collections.singletonList("buffer.putInt(77);");
            }
        } else if (tokens.contains("long")) {
            if (name != null) {
                return Collections.singletonList(
                        "buffer.putLong("
                                + name
                                + (enumClass != null ? "." + enumClass.valuer : "")
                                + ");");
            } else {
                return Collections.singletonList("buffer.putLong(77);");
            }
        } else if (tokens.contains("Ascii8")) {
            var number = findNumber();
            if (number != null) {
                if (name != null) {
                    return Collections.singletonList(
                            "Ascii8.putString(buffer, "
                                    + name
                                    + (enumClass != null ? "." + enumClass.valuer : "")
                                    + ", "
                                    + number + ");");
                } else {
                    return Collections.singletonList(
                            "Ascii8.putString(buffer, \"\", " + number + ");");
                }
            } else {
                if (name != null) {
                    return Collections.singletonList(
                            "Ascii8.putString(buffer, "
                                    + name
                                    + (enumClass != null ? "." + enumClass.valuer : "")
                                    + ");");
                } else {
                    return Collections.singletonList(
                            "Ascii8.putString(buffer, \"\");");
                }
            }
        } else {
            var number = findNumber();
            if (number != null && number != 1) {
                if (name != null) {
                    return Collections.singletonList(
                            "buffer.put("
                                    + name
                                    + (enumClass != null ? "." + enumClass.valuer : "")
                                    + ", 0, "
                                    + number + ");");
                } else {
                    return Collections.singletonList("buffer.put(new byte[" + number + "], 0, " + number + ");");
                }
            } else {
                if (name != null) {
                    return Collections.singletonList("buffer.put("
                            + (tokens.contains("unsigned") ? "(byte) " : "")
                            + name + ");");
                } else {
                    return Collections.singletonList("buffer.put((byte) 77);");
                }
            }
        }
    }

    public String debug() {
        if (name == null) {
            return null;
        }

        return "\"" +  name + "=\" + " + name;
    }
}
