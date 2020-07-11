/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 13:23.
 */
public class ClassInfo<C> implements Serializable {
    private final Class<C> clazz;
    private final Map<Class<? extends Annotation>, Annotation> classAnnotations = new HashMap<>();

    private final int classModifiers;
    private Constructor<C>[] constructors;

    private List<FieldInfo> fields;

    private Set<Method> methods;
    private final Map<Method, Set<Annotation>> methodAnnotationMap = new HashMap<>();

    private List<Type> actualTypes;

    public ClassInfo(Class<C> clazz) {
        this.clazz = clazz;
        this.classModifiers = clazz.getModifiers();

        getClassInfo();
    }

    @SuppressWarnings("unchecked")
    private void getClassInfo() {
        getClassAnnotationsMap();

        fields = new ArrayList<>();
        ReflectionHelper.getDeclaredFields(clazz).forEach(field -> fields.add(new FieldInfo(field)));

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
            for (Annotation annotation : annotations) {
                Set<Annotation> annotationInAnnotation = ReflectionHelper.getClassAnnotations(annotation.annotationType());
                if (!annotationInAnnotation.isEmpty()) {
                    for (Annotation ann : annotationInAnnotation) {
                        Class<? extends Annotation> annotationType = ann.annotationType();
                        if (TypeHelper.filterAnnotation(annotationType)) {
                            this.classAnnotations.put(ann.annotationType(), ann);
                        }
                    }
                }

                this.classAnnotations.put(annotation.annotationType(), annotation);
            }
        }
    }

    private void getMethodAnnotationMap() {
        for (Method method : this.methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();

            if (annotations.length > 0) {
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
                            if (TypeHelper.filterAnnotation(annotationType)) {
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

    public boolean isMethodParameterContainPrimitiveClass() {
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> paramType : parameterTypes) {
                if (TypeHelper.isRawBaseType(paramType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMethodParameterMoreThanOne() {
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length > 1) {
                return true;
            }
        }
        return false;
    }

    public Map<Method, Set<Annotation>> getMethodWithAnnotations() {
        return methodAnnotationMap;
    }

    public boolean hasAnnotatedMethod() {
        return !methodAnnotationMap.isEmpty();
    }

    public Map<Class<? extends Annotation>, Annotation> getClassAnnotations() {
        return classAnnotations;
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getClassAnnotation(Class<T> annotationClass) {
        if (!classAnnotations.isEmpty()) {
            Annotation annotation = classAnnotations.get(annotationClass);
            return (T) annotation;
        }
        return null;
    }

    public <T extends Annotation> Annotation getAnnotatedClassAnnotation(Class<T> annotationClass) {
        if (!classAnnotations.isEmpty()) {
            for (Map.Entry<Class<? extends Annotation>, Annotation> entry : classAnnotations.entrySet()) {
                if (entry.getKey().getAnnotation(annotationClass) != null) {
                    return entry.getValue();
                }
            }
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

    public List<FieldInfo> getFields() {
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

    public boolean isAbstract() {
        return Modifier.isAbstract(classModifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(classModifiers);
    }

    public boolean isAnnotation() {
        return clazz.isAnnotation();
    }

    public boolean isEnum() {
        return clazz.isEnum();
    }

    public boolean isArray() {
        return clazz.isArray();
    }

    public boolean isAssignable(Class<?> superClass) {
        return superClass.isAssignableFrom(clazz);
    }
}
