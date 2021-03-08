package com.iasia.code.example;

import com.iasia.buffer.Ascii8;
import com.iasia.buffer.ByteSerializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//don't edit this file as it is auto-generated by ByteSerializerCodeGenerator
//https://github.com/tommy-iasia/ByteSerializerCodeGenerator

public class Example2 implements ByteSerializable {

    public Example2(int packetSize, int messageCount, int abc, long sequence, long time) {
        this.packetSize = packetSize;
        this.messageCount = messageCount;
        this.abc = abc;
        this.sequence = sequence;
        this.time = time;
    }
    public Example2(int packetSize, int messageCount, String name, int abc, long sequence, long time) {
        this.packetSize = packetSize;
        this.messageCount = messageCount;
        this.name = name;
        this.abc = abc;
        this.sequence = sequence;
        this.time = time;
    }

    public final int packetSize;
    public final int messageCount;
    protected String name;
    public final int abc;
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
        var abc = buffer.getInt();
        buffer.get();
        var sequence = Integer.toUnsignedLong(buffer.getInt());
        var time = buffer.getLong();
    
        return new Example2(packetSize, messageCount, name, abc, sequence, time);
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
        return 2 + 1 + 4 + 4 + 1 + 4 + 8;
    }
    @Override
    public void put(ByteBuffer buffer) {
        buffer.putShort((short) packetSize);
        buffer.put((byte) messageCount);
        Ascii8.putString(buffer, name, 4);
        buffer.putInt(abc);
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
                + "sequence=" + sequence
                + "time=" + time
                + "}";
    }
}
