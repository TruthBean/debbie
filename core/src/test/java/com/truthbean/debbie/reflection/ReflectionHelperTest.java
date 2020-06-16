package com.truthbean.debbie.reflection;

import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.io.StreamHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

public class ReflectionHelperTest {

    @Test
    public void test() {
        ClassLoader classLoader = ReflectionHelperTest.class.getClassLoader();
        ReflectionHelper.newInstance("com.truthbean.debbie.reflection.ReflectionTarget", classLoader);
    }

    @Test
    public void makeDefaultValue() {
        Class<?>[] types = new Class[]{String.class, int.class, float.class, boolean.class};
        Object[] objects = ReflectionHelper.makeDefaultValue(types);
        for (Object object : objects) {
            System.out.println(object);
        }
    }

    @Test
    public void getAllClassByResources() {
        Class<?> clazz = ReflectionHelperTest.class;
        Class<?>[] classes = clazz.getClasses();
        System.out.println(classes);
        ClassLoader classLoader = clazz.getClassLoader();
        var resource = "com/truthbean";
        List<String> resources = ResourcesHandler.getAllClassPathResources(resource, classLoader);
        List<Class<?>> classList = ResourcesHandler.getClassesByResources(resources, classLoader);
        System.out.println(classList.size());
        for (Class<?> aClass : classList) {
            System.out.println(aClass);
        }

        System.out.println("------------------------------------");
        ResourceResolver resourceResolver = new ResourceResolver();
        resources = ResourcesHandler.getAllClassPathResources("", classLoader);
        resourceResolver.addResource(resources);
        classList = ReflectionHelper.getAllClassByPackageName("com.truthbean", classLoader, resourceResolver);
        System.out.println(classList.size());
        for (Class<?> aClass : classList) {
            System.out.println(aClass);
        }
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
        Set<Method> declaredMethods = ReflectionHelper.getDeclaredMethods(clazz);
        // Method[] declaredMethods = clazz.getMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println(declaredMethod.getName() + "-----------------------------------------");
            Type[] methodActualTypes = ReflectionHelper.getMethodActualTypes(declaredMethod, clazz);
            for (Type methodActualType : methodActualTypes) {
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
