package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;

public class ClassInstanceTransformer implements DataTransformer<Object, String> {
    @Override
    public String transform(Object o) {
        return o.getClass().getName();
    }

    @Override
    public Object reverse(String className) {
        ClassLoader defaultClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        Class<?> clazz = null;
        try {
            clazz = defaultClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ReflectionHelper.newInstance(clazz);
    }
}
