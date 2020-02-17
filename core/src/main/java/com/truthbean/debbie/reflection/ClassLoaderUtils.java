package com.truthbean.debbie.reflection;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:39.
 */
public class ClassLoaderUtils {
    private ClassLoaderUtils() {
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassLoaderUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getPlatformClassLoader();
                } catch (Throwable ignored1) {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    } catch (Throwable ignored2) {
                        // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                    }
                }
            }
        }
        return cl;
    }

    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = clazz.getClassLoader();
        } catch (Throwable e) {
            // SecurityException
        }
        if (cl == null) {
            // getClassLoader() returning null indicates the bootstrap ClassLoader
            try {
                cl = Thread.currentThread().getContextClassLoader();
            } catch (Throwable ex) {
                // Cannot access thread context ClassLoader - falling back...
            }
            if (cl == null) {
                // No thread context class loader -> use class loader of this class.
                try {
                    cl = ClassLoader.getPlatformClassLoader();
                } catch (Throwable ignored1) {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    } catch (Throwable ignored2) {
                        // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                    }
                }
            }
        }
        return cl;
    }
}
