package com.iasia.code.example;

public enum ExampleEnum2 {

    None(new byte[0]),
    A(new byte[] { (byte) 1, (byte) 2}),
    B(new byte[] { (byte) 2, (byte) 3}),
    C(new byte[] { (byte) 3, (byte) 4});

    private final byte[] value;
    public final byte[] value() {
        return value;
    }

    ExampleEnum2(byte[] value) {
        this.value = value;
    }

    public static ExampleEnum2 valueOf(byte[] value) {
        if (value.length == 2 && value[0] == 1 && value[1] == 2) {
            return A;
        } else if (value.length == 2 && value[0] == 2 && value[1] == 3) {
            return B;
        } else if (value.length == 2 && value[0] == 3 && value[1] == 4) {
            return C;
        } else {
            return None;
        }
    }
}
