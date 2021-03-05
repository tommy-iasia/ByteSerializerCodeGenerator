package com.iasia.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Ascii8 {

    public static String getString(ByteBuffer buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);

        return new String(bytes, StandardCharsets.ISO_8859_1);
    }
    public static ByteBuffer putString(ByteBuffer buffer, String string, int length) {
        var bytes = string.getBytes(StandardCharsets.ISO_8859_1);
        if (bytes.length == length) {
            buffer.put(bytes);
        } else if (bytes.length > length) {
            buffer.put(bytes, 0, length);
        } else {
            buffer.put(bytes);

            var zeros = new byte[length - bytes.length];
            buffer.put(zeros);
        }

        return buffer;
    }

    public static String getString(ByteBuffer buffer) {
        var from = buffer.position();

        var length = 0;
        for (var i = buffer.limit() - from; i > 0; i--) {
            if (buffer.get() == 0) {
                break;
            }

            length++;
        }

        var slice = buffer.slice(from, length);
        return getString(slice, length);
    }
    public static ByteBuffer putString(ByteBuffer buffer, String string) {
        var bytes = string.getBytes(StandardCharsets.ISO_8859_1);
        buffer.put(bytes);
        buffer.put((byte) 0);

        return buffer;
    }
}
