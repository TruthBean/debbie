package com.truthbean.debbie.reflection;

import java.security.SecureClassLoader;

public class ByteArrayClassLoader extends SecureClassLoader {
    public ByteArrayClassLoader(ClassLoader superClassLoader) {
        super(superClassLoader);
    }

    public Class<?> defineClass(String name, byte[] code) {
        if (name == null) {
            throw new IllegalArgumentException("");
        }
        return defineClass(name, code, 0, code.length);
    }
}
