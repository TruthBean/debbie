/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * <a href="http://license.coscl.org.cn/MulanPSL2">http://license.coscl.org.cn/MulanPSL2</a>
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 19:17.
 */
public interface BeanAnnotationManager {

    <A extends Annotation> void registerInjectType(Class<A> injectType);

    void registerInjectType(Set<Class<? extends Annotation>> injectTypes);

    /**
     * @param <A> annotation type
     * @param annotationType registered by
     *      @see BeanComponent
     * @param parser component parser
     * 标识的 annotation
     */
    <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser);

    Set<Class<? extends Annotation>> getBeanAnnotations();

    Set<Class<? extends Annotation>> getClassAnnotation();

    Set<Class<? extends Annotation>> getMethodAnnotation();

    boolean support(Class<?> beanClass);

    Set<Class<? extends Annotation>> getInjectTypes();

    Class<? extends Annotation> getInjectType();

    default boolean hasInjectType(AnnotatedElement annotatedElement) {
        return hasInjectType(annotatedElement, false);
    }

    boolean hasInjectType(AnnotatedElement annotatedElement, boolean another);

    default boolean isRequired(AnnotatedElement annotatedElement, boolean another) {
        Boolean required = injectBeanRequiredIfPresent(annotatedElement, another);
        if (required == null) {
            return another;
        }
        return required;
    }

    boolean injectedRequired(Annotation annotation, boolean another);

    Boolean injectBeanRequiredIfPresent(AnnotatedElement annotatedElement, boolean another);

    boolean containInjectType(Class<? extends Annotation> annotation);

    void addIgnoreInterface(Class<?> ignoreInterface);

    Set<Class<?>> getIgnoreInterface();

    void addIgnoreAnnotation(Class<? extends Annotation> annotation);

    Set<Class<? extends Annotation>> getIgnoredAnnotations();
}
