package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.1
 */
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
            LOGGER.error("", e);
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassTransformer.class);
}
