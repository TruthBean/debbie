package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.1
 */
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
            logger.error("", e);
        }
        return ReflectionHelper.newInstance(clazz);
    }

    private static final Logger logger = LoggerFactory.getLogger(ClassInstanceTransformer.class);
}
