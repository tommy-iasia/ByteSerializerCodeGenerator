package com.iasia.example;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Example1 {

    public Example1() { }
    public Example1(int abc, String bcd) {
        this.abc = abc;
        this.bcd = bcd;
    }

    public int abc;
    public String bcd;

    public static Example1 get(ByteBuffer buffer) {
        var abc = buffer.getInt();
        var bcd = com.iasia.buffer.Ascii8.getString(buffer, 5);
    
        return new Example1(abc, bcd);
    }

    public ByteBuffer put() {
        var length = length();
        var buffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
    
        put(buffer);
    
        return buffer;
    }
    public int length() {
        return 4 + 5;
    }
    public void put(ByteBuffer buffer) {
        buffer.putInt(abc);
        com.iasia.buffer.Ascii8.putString(buffer, bcd, 5);
    }
}
