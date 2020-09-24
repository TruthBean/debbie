/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.data.transformer.DataTransformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:47.
 */
public interface BeanInitialization {
    /**
     * @param <A> annotation type
     * @param annotationType registered by
     *      @see BeanComponent
     * 标识的 annotation
     */
    <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser);

    void registerBeanConfiguration(Collection<Class<?>> classes);

    <D extends DataTransformer<?,?>> void registerDataTransformer(D dataTransformer, Type argsType1, Type argsType2);

    <D extends DataTransformer<?,?>> void registerDataTransformer(D transformer);

    <O, T> T transform(final O origin, final Class<T> target);

    void init(Class<?> beanClass) ;

    void initBean(DebbieBeanInfo<?> beanInfo);

    void initSingletonBean(DebbieBeanInfo<?> beanInfo);

    void refreshSingletonBean(DebbieBeanInfo<?> beanInfo);

    void refreshBean(DebbieBeanInfo<?> beanInfo);

    void init(Set<Class<?>> beanClasses);

    void init(ClassLoader classLoader, String... packageName);

    <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass);

    Set<DebbieBeanInfo<?>> getAnnotatedBeans();

    <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedMethodBean(Class<T> annotationClass);

    Set<DebbieBeanInfo<?>> getBeanByInterface(Class<?> interfaceType);

    Set<DebbieBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType);

    Set<Class<? extends Annotation>> getBeanAnnotations();

    <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean);

    <Bean> Bean getRegisterBean(Class<Bean> bean) ;

    Set<DebbieBeanInfo<?>> getRegisteredBeans();

    Set<DebbieBeanInfo<?>> getRegisteredRawBeans();

    Set<Class<?>> getRegisteredRawBeanType();

    <Bean> Set<Method> getBeanMethods(Class<Bean> beanClass);

    void reset();

}
