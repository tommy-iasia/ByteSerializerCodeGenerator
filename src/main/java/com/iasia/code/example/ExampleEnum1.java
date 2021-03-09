package com.iasia.code.example;

public enum ExampleEnum1 {

    A((short) 1),
    B((short) 2),
    C((short) 3);

    public final short value;
    ExampleEnum1(short value) {
        this.value = value;
    }

    public static ExampleEnum1 valueOf(short value) {
        return switch (value) {
            case (short) 1 -> A;
            case (short) 2 -> B;
            default -> C;
        };
    }
}
