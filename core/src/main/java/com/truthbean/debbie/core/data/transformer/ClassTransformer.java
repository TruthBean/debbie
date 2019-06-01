package com.truthbean.debbie.core.data.transformer;

import com.truthbean.debbie.core.reflection.ClassLoaderUtils;

public class ClassTransformer implements DataTransformer<Class, String> {
    @Override
    public String transform(Class aClass) {
        return aClass.getName();
    }

    @Override
    public Class reverse(String s) {
        ClassLoader defaultClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        try {
            return defaultClassLoader.loadClass(s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}