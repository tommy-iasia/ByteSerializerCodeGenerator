package com.iasia;

import com.iasia.code.DataClass;

import java.io.IOException;

public class Program {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void main(String[] args) throws IOException {
        DataClass.write("src\\main\\java\\com\\iasia\\code\\example\\Example1.yaml");
        DataClass.write("src\\main\\java\\com\\iasia\\code\\example\\Example2.yaml");
    }
}
