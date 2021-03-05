package com.iasia.code.test;

import com.iasia.code.example.Example1;
import com.iasia.code.example.Example2;

public class TestRead {

    public static void main(String[] args) {
        test1();
        test2();
    }

    private static void test1() {
        System.out.println("test read 1");

        var from = new Example1(123, "456");

        var buffer = from.put();
        var to = Example1.get(buffer);

        assert from.abc == to.abc;
        assert from.bcd.equals(to.bcd);
    }

    private static void test2() {
        System.out.println("test read 2");

        var from = new Example2(123, 255, "abc", 789, 012,345678);

        var buffer = from.put();
        var to = Example2.get(buffer);

        assert from.packetSize == to.packetSize;
        assert from.messageCount == to.messageCount;
        assert from.getName().equals(to.getName());
        assert from.abc == to.abc;
        assert from.sequence() == to.sequence();
        assert from.time == to.time;
    }
}
