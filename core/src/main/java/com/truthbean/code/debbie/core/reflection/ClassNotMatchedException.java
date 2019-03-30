package com.truthbean.code.debbie.core.reflection;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/18 23:10.
 */
public class ClassNotMatchedException extends RuntimeException {
    public ClassNotMatchedException(Class<?> expectedClass, Class<?> clazz) {
        super(clazz.getCanonicalName() + " not matched " + expectedClass.getCanonicalName());
    }
}
