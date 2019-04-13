package com.truthbean.code.debbie.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 13:23.
 */
public class ClassInfo {
    private Class<?> clazz;
    private Map<Class<? extends Annotation>, Annotation> classAnnotations = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private Constructor[] constructors;

    private List<Field> fields;

    private List<Method> methods;
    private Map<? extends Annotation, List<Method>> methodWithAnnotation;

    public ClassInfo(Class<?> clazz) {
        this.clazz = clazz;

        var annotations = clazz.getAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                classAnnotations.put(annotation.annotationType(), annotation);
            }
        }

        fields = ReflectionHelper.getDeclaredFields(clazz);

        this.constructors = clazz.getConstructors();
        this.methods = ReflectionHelper.getDeclaredMethods(clazz);

        this.methodWithAnnotation = new HashMap<>();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @SuppressWarnings("rawtypes")
    public Constructor[] getConstructors() {
        return constructors;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Map<Class<? extends Annotation>, Annotation> getClassAnnotations() {
        return classAnnotations;
    }

    public Map<? extends Annotation, List<Method>> getMethodWithAnnotation() {
        return methodWithAnnotation;
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
        return getClazz().equals(classInfo.getClazz());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClazz());
    }
}
