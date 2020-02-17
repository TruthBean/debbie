package com.truthbean.debbie.reflection;

import com.truthbean.debbie.io.StreamHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.jar.JarFile;

public class ReflectionHelperTest {

    @Test
    public void test() {
        ClassLoader classLoader = ReflectionHelperTest.class.getClassLoader();
        ReflectionHelper.newInstance("com.truthbean.debbie.reflection.ReflectionTarget", classLoader);
    }

    @Test
    public void getAllClassByResources() {
        Class<?> clazz = ReflectionHelperTest.class;
        Class<?>[] classes = clazz.getClasses();
        System.out.println(classes);
        ClassLoader classLoader = clazz.getClassLoader();
        List<Class<?>> classList = ReflectionHelper.getAllClassByResources(".", classLoader);
        System.out.println(classList);
    }

    @Test
    public void getAllClass() throws IOException {
        ClassLoader classLoader = ReflectionHelperTest.class.getClassLoader();
        String jar = "D:\\develop\\java\\debbie-cms\\target\\debbie-cms-0.0.2-SNAPSHOT-fat.jar";
        File file = new File(jar);
        JarFile jarFile = new JarFile(file);
        URL url = new URL("file://" + jar);
        List<Class<?>> allClassFromJar = StreamHelper.getAllClassFromJar(jarFile, classLoader);
        for (Class<?> clazz : allClassFromJar) {
            System.out.println(clazz);
        }
    }

    @Test
    public void getMethodActualTypes() throws ClassNotFoundException {
        Class<?> clazz = ClassLoaderUtils.getDefaultClassLoader().loadClass("com.truthbean.debbie.reflection.TestParameterized");
        List<Method> declaredMethods = ReflectionHelper.getDeclaredMethods(clazz);
        // Method[] declaredMethods = clazz.getMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println(declaredMethod.getName() + "-----------------------------------------");
            Class<?>[] methodActualTypes = ReflectionHelper.getMethodActualTypes(declaredMethod, clazz);
            for (Class<?> methodActualType : methodActualTypes) {
                System.out.println(methodActualType);
            }

            /*Parameter[] parameters = declaredMethod.getParameters();

            for (Parameter parameter : parameters) {
                System.out.println(parameter.getName());
                System.out.println(parameter.getType());
            }*/

            System.out.println("===========================================");
        }
    }
}
