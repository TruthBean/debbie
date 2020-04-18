package com.truthbean.debbie.reflection;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2019-12-03 16:41
 */
class ClassLoaderUtilsTest {

    public static void main(String[] args) {
        Class<?> clazz = ClassLoaderUtilsTest.class;
        ClassLoader classLoader = clazz.getClassLoader();

        String userPath = System.getProperty("user.dir");

        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathGetClass = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        String classLoaderPath = classLoader.toString();

        System.out.println("userPath: " + userPath);
        System.out.println("path: " + path);
        System.out.println("pathGetClass: " + pathGetClass);
        System.out.println("classLoaderPath: " + classLoaderPath);

        ClassLoader loader = ClassLoaderUtilsTest.class.getClassLoader();
        URL resource = loader.getResource(".");
        System.out.println(resource);
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{resource});
        URL[] urls = urlClassLoader.getURLs();
        for (URL url : urls) {
            System.out.println(url);
        }
    }
}