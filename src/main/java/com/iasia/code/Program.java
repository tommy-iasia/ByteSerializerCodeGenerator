package com.iasia.code;

public class Program {

    public static void main(String... args) {
        System.out.println("write " + args.length + " files");

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
