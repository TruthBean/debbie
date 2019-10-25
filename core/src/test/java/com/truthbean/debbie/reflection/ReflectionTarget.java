package com.truthbean.debbie.reflection;

public class ReflectionTarget {
    private int m = method();

    static {
        System.out.println("static block");
    }

    private ReflectionTarget() {
        System.out.println("private constructor.");
    }

    private static int sm = staticMethod();

    private int method() {
        System.out.println("method");
        return 0;
    }

    {
        System.out.println("block");
    }

    private static int staticMethod() {
        System.out.println("static method");
        return 0;
    }
}
