package com.truthbean.debbie.core.data.transformer;

import com.truthbean.debbie.core.reflection.ClassLoaderUtils;
import com.truthbean.debbie.core.reflection.ReflectionHelper;

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
