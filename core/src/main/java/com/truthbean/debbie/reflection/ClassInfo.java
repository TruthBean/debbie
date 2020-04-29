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
    private final Map<Class<? extends Annotation>, Annotation> classAnnotations = new HashMap<>();

    private Constructor<C>[] constructors;

    private List<Field> fields;

    private Set<Method> methods;
    private final Map<Method, Set<Annotation>> methodAnnotationMap = new HashMap<>();

    private List<Type> actualTypes;

    public ClassInfo(Class<C> clazz) {
        this.clazz = clazz;

        getClassInfo();
    }

    @SuppressWarnings("unchecked")
    private void getClassInfo() {
        getClassAnnotationsMap();

        fields = ReflectionHelper.getDeclaredFields(clazz);

        this.constructors = (Constructor<C>[]) clazz.getConstructors();
        this.methods = ReflectionHelper.getDeclaredMethods(clazz);
        getMethodAnnotationMap();

        Type[] actualTypes = ReflectionHelper.getActualTypes(clazz);
        if (actualTypes != null) {
            this.actualTypes = Arrays.asList(actualTypes);
        }
    }

    private void getClassAnnotationsMap() {
        Set<Annotation> annotations = ReflectionHelper.getClassAnnotations(clazz);

        if (!annotations.isEmpty()) {
            Set<Annotation> annotationsCopy = new HashSet<>(annotations);
            for (Annotation annotation : annotationsCopy) {
                Set<Annotation> annotationInAnnotation = ReflectionHelper.getClassAnnotations(annotation.annotationType());
                if (!annotationInAnnotation.isEmpty()) {
                    for (Annotation ann : annotationInAnnotation) {
                        Class<? extends Annotation> annotationType = ann.annotationType();
                        if (!Target.class.equals(annotationType) && !Retention.class.equals(annotationType) &&
                                !Repeatable.class.equals(annotationType) && !Inherited.class.equals(annotationType) &&
                                !Documented.class.equals(annotationType) && !Deprecated.class.equals(annotationType) &&
                                !FunctionalInterface.class.equals(annotationType) &&
                                !SuppressWarnings.class.equals(annotationType)) {
                            annotations.add(ann);
                        }
                    }
                }
            }
        }

        if (!annotations.isEmpty()) {
            for (Annotation annotation : annotations) {
                this.classAnnotations.put(annotation.annotationType(), annotation);
            }
        }
    }

    private void getMethodAnnotationMap() {
        for (Method method : this.methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();

            if (annotations != null && annotations.length > 0) {
                Set<Annotation> methodAnnotations = new HashSet<>();
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> type = annotation.annotationType();
                    if (type == Override.class || type == SuppressWarnings.class || type == Deprecated.class
                            || type == SafeVarargs.class) {
                        continue;
                    }
                    methodAnnotations.add(annotation);
                    Set<Annotation> annotationInAnnotation = ReflectionHelper.getClassAnnotations(type);
                    if (!annotationInAnnotation.isEmpty()) {
                        for (Annotation ann : annotationInAnnotation) {
                            Class<? extends Annotation> annotationType = ann.annotationType();
                            if (!Target.class.equals(annotationType) && !Retention.class.equals(annotationType) &&
                                    !Repeatable.class.equals(annotationType) && !Inherited.class.equals(annotationType) &&
                                    !Documented.class.equals(annotationType) && !Deprecated.class.equals(annotationType) &&
                                    !FunctionalInterface.class.equals(annotationType) &&
                                    !SuppressWarnings.class.equals(annotationType)) {
                                methodAnnotations.add(ann);
                            }
                        }
                    }
                    if (!methodAnnotations.isEmpty()) {
                        this.methodAnnotationMap.put(method, methodAnnotations);
                    }
                }
            }
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

    public Set<Method> getMethods() {
        return methods;
    }

    public Map<Method, Set<Annotation>> getMethodWithAnnotations() {
        return methodAnnotationMap;
    }

    public Map<Class<? extends Annotation>, Annotation> getClassAnnotations() {
        return classAnnotations;
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getClassAnnotation(Class<T> annotationClass) {
        if (classAnnotations != null && !classAnnotations.isEmpty()) {
            Annotation annotation = classAnnotations.get(annotationClass);
            return (T) annotation;
        }
        return null;
    }

    public Set<Method> getAnnotationMethod(Class<? extends Annotation> annotationType) {
        Set<Method> methods = new HashSet<>();
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
        ClassInfo<?> classInfo = (ClassInfo<?>) o;
        return clazz.getName().equals(classInfo.getClazz().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz.getName());
    }

    public ClassInfo<C> copy() {
        return new ClassInfo<>(clazz);
    }

    public boolean isInterface() {
        return clazz.isInterface();
    }
}
