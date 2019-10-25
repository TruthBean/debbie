package com.truthbean.debbie.reflection;

public class ByteArrayClassLoader extends ClassLoader {
    public ByteArrayClassLoader() {
        super(ByteArrayClassLoader.class.getClassLoader());
    }

    public synchronized Class<?> getClass(String name, byte[] code) {
        if (name == null) {
            throw new IllegalArgumentException("");
        }
        return defineClass(name, code, 0, code.length);
    }
}
