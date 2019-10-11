package com.truthbean.debbie.reflection;

import java.io.Serializable;
import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 13:23.
 */
public class ClassInfo<C> implements Serializable {
    private Class<C> clazz;
    private Map<Class<? extends Annotation>, Annotation> classAnnotations = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private Constructor<C>[] constructors;

    private List<Field> fields;

    private List<Method> methods;

    private List<Type> actualTypes;

    public ClassInfo(Class<C> clazz) {
        this.clazz = clazz;

        getClassInfo();
    }

    @SuppressWarnings("unchecked")
    private void getClassInfo() {
        Set<Annotation> annotations = ReflectionHelper.getClassAnnotations(clazz);

        if (annotations != null && !annotations.isEmpty()) {
            Set<Annotation> annotationsCopy = new HashSet<>(annotations);
            for (Annotation annotation : annotationsCopy) {
                Set<Annotation> annotationInAnnotation = ReflectionHelper.getClassAnnotations(annotation.annotationType());
                if (annotationInAnnotation != null && !annotationInAnnotation.isEmpty()) {
                    for (Annotation ann : annotationInAnnotation) {
                        Class<? extends Annotation> annotationType = ann.annotationType();
                        if (!Target.class.equals(annotationType) && !Retention.class.equals(annotationType) &&
                            !Repeatable.class.equals(annotationType) && !Inherited.class.equals(annotationType) &&
                            !Documented.class.equals(annotationType)) {
                            annotations.add(ann);
                        }
                    }
                }
            }
        }

        if (annotations != null && !annotations.isEmpty()) {
            for (Annotation annotation : annotations) {
                this.classAnnotations.put(annotation.annotationType(), annotation);
            }
        }

        fields = ReflectionHelper.getDeclaredFields(clazz);

        this.constructors = (Constructor<C>[]) clazz.getConstructors();
        this.methods = ReflectionHelper.getDeclaredMethods(clazz);

        Type[] actualTypes = ReflectionHelper.getActualTypes(clazz);
        if (actualTypes != null) {
            this.actualTypes = Arrays.asList(actualTypes);
        }
    }

    public Class<C> getClazz() {
        return clazz;
    }

    public Constructor<C>[] getConstructors() {
        return constructors;
    }

    public List<Type> getActualTypes() {
        return actualTypes;
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
        return clazz.getName().equals(classInfo.getClazz().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz.getName());
    }

    public ClassInfo<C> copy() {
        return new ClassInfo<>(clazz);
    }
}
