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
     * @param parser component parser
     * 标识的 annotation
     */
    <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser);

    void registerBeanConfiguration(Collection<Class<?>> classes);

    <D extends DataTransformer<?,?>> void registerDataTransformer(D dataTransformer, Type argsType1, Type argsType2);

    <D extends DataTransformer<?,?>> void registerDataTransformer(D transformer);

    <O, T> T transform(final O origin, final Class<T> target);

    void init(Class<?> beanClass);

    void initBean(BeanInfo<?> beanInfo);

    void initSingletonBean(MutableBeanInfo<?> beanInfo);

    void refreshSingletonBean(MutableBeanInfo<?> beanInfo);

    void refreshBean(MutableBeanInfo<?> beanInfo);

    void init(Set<Class<?>> beanClasses);

    void init(ClassLoader classLoader, String... packageName);

    <T extends Annotation> Set<DebbieClassBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass);

    Set<DebbieClassBeanInfo<?>> getAnnotatedBeans();

    <T extends Annotation> Set<DebbieClassBeanInfo<?>> getAnnotatedMethodBean(Class<T> annotationClass);

    Set<DebbieClassBeanInfo<?>> getBeanByInterface(Class<?> interfaceType);

    Set<DebbieClassBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType);

    Set<Class<? extends Annotation>> getBeanAnnotations();

    <Bean> MutableBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean);

    <Bean> Bean getRegisterBean(Class<Bean> bean) ;

    Set<BeanInfo<?>> getRegisteredBeans();

    Set<MutableBeanInfo<?>> getRegisteredRawBeans();

    Set<Class<?>> getRegisteredRawBeanType();

    <Bean> Set<Method> getBeanMethods(Class<Bean> beanClass);

    void reset();

}
