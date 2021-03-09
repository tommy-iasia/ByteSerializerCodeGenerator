package com.iasia.code;

public class DataEnum {

    public final String packageName;
    public final String className;

    public final String parser;
    public final String valuer;

    public DataEnum(String packageName, String className, String parser, String valuer) {
        this.packageName = packageName;
        this.className = className;
        this.parser = parser;
        this.valuer = valuer;
    }
}
