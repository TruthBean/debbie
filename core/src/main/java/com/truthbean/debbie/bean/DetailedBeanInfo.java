/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.FieldInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-28 16:57
 */
public interface DetailedBeanInfo<Bean> extends BeanInfo<Bean> {

    <T> Class<T> getBeanClass();

    Boolean getLazyCreate();

    <T> Class<T> getBeanInterface();

    Set<Method> getMethods();

    boolean isMethodParameterContainPrimitiveClass();

    boolean isMethodParameterMoreThanOne();

    Map<Method, Set<Annotation>> getMethodWithAnnotations();

    boolean hasAnnotatedMethod();

    Map<Class<? extends Annotation>, Annotation> getClassAnnotations();

    <T extends Annotation> T getClassAnnotation(Class<T> annotationClass);

    <T extends Annotation> boolean containClassAnnotation(Class<T> annotationClass);

    <T extends Annotation> boolean containMethodAnnotation(Class<T> annotationType, Method target);

    <T extends Annotation> Annotation getAnnotatedClassAnnotation(Class<T> annotationClass);

    Map<Class<? extends Annotation>, Annotation> getClassAnnotatedAnnotationOrClassAnnotation(Class<? extends Annotation> annotationClass);

    Set<Method> getAnnotationMethod(Class<? extends Annotation> annotationType);

    List<FieldInfo> getFields();

    boolean isInterface();

    boolean isAbstract();

    boolean isFinal();

    boolean isAnnotation();

    boolean isEnum();

    boolean isArray();

    boolean isAssignable(Class<?> superClass);
}
