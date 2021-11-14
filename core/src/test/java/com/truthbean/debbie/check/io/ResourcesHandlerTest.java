package com.truthbean.debbie.check.io;

import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.net.uri.AntPathMatcher;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-03-23 20:43
 */
class
ResourcesHandlerTest {

    @Test
    void handleStaticResource() {
        ResourcesHandlerTest test = new ResourcesHandlerTest();
        var classLoader = ClassLoaderUtils.getClassLoader(ResourcesHandlerTest.class);
        System.out.println(classLoader);
        //test.getResources(classLoader);
        List<String> resources = ResourcesHandler.getAllClassPathResources("", classLoader);
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String resource : resources) {
            boolean match = pathMatcher.match("**/*.txt", resource);
            System.out.println(match);
            System.out.println(resource);
        }
    }

    void getResources(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassLoaderUtils.getDefaultClassLoader();
        }
        final ClassLoader finalClassLoader = classLoader;
        classLoader.resources("").forEach((url) -> {
            System.out.println(url);
            findResources(new File(url.getFile()), "");
        });
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */

    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;

        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes.toArray(new Class[classes.size()]);
    }


    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static List<String> findResources(File directory, String packageName) {
        List<String> classes = new ArrayList<String>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findResources(file, packageName + "/" + file.getName()));
            } else {
                var resource = packageName + '/' + file.getName().substring(0, file.getName().length() - 6);
                System.out.println(resource);
                classes.add(resource);
            }
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}