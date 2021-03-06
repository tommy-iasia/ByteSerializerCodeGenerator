package com.iasia.code.example;

import com.iasia.buffer.Ascii8;
import com.iasia.buffer.ByteSerializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//don't edit this file as it is auto-generated by ByteSerializerCodeGenerator
//instead, edit the source file Example2.yaml
//https://github.com/tommy-iasia/ByteSerializerCodeGenerator

public class Example2 implements ByteSerializable {

    public final static String CLASS_NAME = "Example2";

    public Example2(int packetSize, int messageCount, ExampleEnum1 abc, ExampleEnum2 bcd, long sequence, long time) {
        this.packetSize = packetSize;
        this.messageCount = messageCount;
        this.abc = abc;
        this.bcd = bcd;
        this.sequence = sequence;
        this.time = time;
    }
    public Example2(int packetSize, int messageCount, String name, ExampleEnum1 abc, ExampleEnum2 bcd, long sequence, long time) {
        this.packetSize = packetSize;
        this.messageCount = messageCount;
        this.name = name;
        this.abc = abc;
        this.bcd = bcd;
        this.sequence = sequence;
        this.time = time;
    }

    public final int packetSize;
    public final int messageCount;
    protected String name;
    public final ExampleEnum1 abc;
    public final ExampleEnum2 bcd;
    private final long sequence;
    public final long time;

    public String getName() {
        return name;
    }

    public long sequence() {
        return sequence;
    }

    public static Example2 get(ByteBuffer buffer) {
        var packetSize = Short.toUnsignedInt(buffer.getShort());
        var messageCount = Byte.toUnsignedInt(buffer.get());
        var name = Ascii8.getString(buffer, 4).trim();
        var abc = ExampleEnum1.valueOf(buffer.getShort());
        var bcd = ExampleEnum2.valueOf(ByteSerializable.getBytes(buffer, 2));
        buffer.get();
        var sequence = Integer.toUnsignedLong(buffer.getInt());
        var time = buffer.getLong();
    
        return new Example2(packetSize, messageCount, name, abc, bcd, sequence, time);
    }

    @Override
    public ByteBuffer put() {
        var length = length();
        var buffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
    
        put(buffer);
    
        buffer.flip();
        return buffer;
    }
    @Override
    public int length() {
        return 2 + 1 + 4 + 2 + 2 + 1 + 4 + 8;
    }
    @Override
    public void put(ByteBuffer buffer) {
        buffer.putShort((short) packetSize);
        buffer.put((byte) messageCount);
        Ascii8.putString(buffer, name, 4);
        buffer.putShort(abc.value);
        buffer.put(bcd.value(), 0, 2);
        buffer.put((byte) 77);
        buffer.putInt((int) sequence);
        buffer.putLong(time);
    }

    @Override
    public String toString() {
        return "Example2{"
                + "packetSize=" + packetSize
                + "messageCount=" + messageCount
                + "name=" + name
                + "abc=" + abc
                + "bcd=" + bcd
                + "sequence=" + sequence
                + "time=" + time
                + "}";
    }
}
