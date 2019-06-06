package com.truthbean.debbie.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 13:23.
 */
public class ClassInfo<C> {
    private Class<C> clazz;
    private Map<Class<? extends Annotation>, Annotation> classAnnotations = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private Constructor<C>[] constructors;

    private List<Field> fields;

    private List<Method> methods;
    // private Map<? extends Annotation, List<Method>> methodWithAnnotation;

    public ClassInfo(Class<C> clazz) {
        this.clazz = clazz;

        var annotations = clazz.getAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                classAnnotations.put(annotation.annotationType(), annotation);
            }
        }

        fields = ReflectionHelper.getDeclaredFields(clazz);

        this.constructors = (Constructor<C>[]) clazz.getConstructors();
        this.methods = ReflectionHelper.getDeclaredMethods(clazz);

        /*this.methodWithAnnotation = new HashMap<>();
        if (this.methods != null && !this.methods.isEmpty()) {
            for (Method method : this.methods) {
                Annotation[] methodAnnotations = method.getDeclaredAnnotations();
                if (methodAnnotations != null && methodAnnotations.length > 0) {
                    for (Annotation methodAnnotation : methodAnnotations) {
                        List<Method> methods = methodWithAnnotation.get(methodAnnotation);
                        if (methods == null) {
                            methods = new ArrayList<>();
                        }
                        methods.add(method);
                        methodWithAnnotation.put(methodAnnotation.annotationType(), methods);
                    }
                }
            }
        }*/
    }

    public Class<C> getClazz() {
        return clazz;
    }

    public Constructor<C>[] getConstructors() {
        return constructors;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Map<Class<? extends Annotation>, Annotation> getClassAnnotations() {
        return classAnnotations;
    }

    public Annotation getClassAnnotation(Class<? extends Annotation> annotationClass) {
        if (classAnnotations != null && !classAnnotations.isEmpty()) {
            return classAnnotations.get(annotationClass);
        }
        return null;
    }

    /*public Map<? extends Annotation, List<Method>> getMethodWithAnnotation() {
        return methodWithAnnotation;
    }*/

    public List<Method> getAnnotationMethod(Class<? extends Annotation> annotationType) {
        List<Method> methods = new ArrayList<>();
        for (Method method : this.methods) {
            Annotation annotation = method.getAnnotation(annotationType);
            if (annotation != null) {
                methods.add(method);
            }
        }
        return methods;
    }

    public List<Field> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassInfo)) {
            return false;
        }
        ClassInfo classInfo = (ClassInfo) o;
        return clazz.getName().equals(clazz.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz.getName());
    }
}
