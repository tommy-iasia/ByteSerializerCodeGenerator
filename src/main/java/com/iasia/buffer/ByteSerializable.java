package com.iasia.buffer;

import java.nio.ByteBuffer;

public interface ByteSerializable {
    ByteBuffer put();

    int length();
    void put(ByteBuffer buffer);

    static byte[] getBytes(ByteBuffer buffer, int length) {
        var bytes = new byte[length];
        buffer.get(bytes);

        return bytes;
    }
}
