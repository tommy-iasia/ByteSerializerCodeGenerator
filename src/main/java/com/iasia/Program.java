package com.iasia;

import com.iasia.code.DataClass;

public class Program {

    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println("write " + arg);

            try {
                DataClass.write(arg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
