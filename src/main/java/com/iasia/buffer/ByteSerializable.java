package com.iasia.buffer;

import java.nio.ByteBuffer;

public interface ByteSerializable {
    ByteBuffer put();

    int length();
    void put(ByteBuffer buffer);
}
