package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.*;
import com.truthbean.transformer.DataTransformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 */
class EmptyBeanInitialization implements BeanInitialization {
    @Override
    public <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser) {

    }

    @Override
    public void registerBeanConfiguration(Collection<Class<?>> classes) {

    }

    @Override
    public <D extends DataTransformer<?, ?>> void registerDataTransformer(D dataTransformer, Type argsType1, Type argsType2) {

    }

    @Override
    public <D extends DataTransformer<?, ?>> void registerDataTransformer(D transformer) {

    }

    @Override
    public <O, T> T transform(O origin, Class<T> target) {
        return null;
    }

    @Override
    public void init(Class<?> beanClass) {
    }

    @Override
    public void initBean(BeanInfo<?> beanInfo) {

    }

    @Override
    public BeanInitialization initBeanInfo(BeanInfo<?> beanInfo) {
        return this;
    }

    @Override
    public void initSingletonBean(MutableBeanInfo<?> beanInfo) {

    }

    @Override
    public void refreshSingletonBean(MutableBeanInfo<?> beanInfo) {

    }

    @Override
    public void refreshBean(MutableBeanInfo<?> beanInfo) {

    }

    @Override
    public void init(Set<Class<?>> beanClasses) {

    }

    @Override
    public void init(ClassLoader classLoader, String... packageName) {

    }

    @Override
    public <T extends Annotation> Set<DebbieClassBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        return new HashSet<>();
    }

    @Override
    public Set<DebbieClassBeanInfo<?>> getAnnotatedBeans() {
        return new HashSet<>();
    }

    @Override
    public <T extends Annotation> Set<DebbieClassBeanInfo<?>> getAnnotatedMethodBean(Class<T> annotationClass) {
        return new HashSet<>();
    }

    @Override
    public Set<DebbieClassBeanInfo<?>> getBeanByInterface(Class<?> interfaceType) {
        return new HashSet<>();
    }

    @Override
    public Set<DebbieClassBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
        return new HashSet<>();
    }

    @Override
    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return new HashSet<>();
    }

    @Override
    public <Bean> MutableBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        return null;
    }

    @Override
    public <Bean> Bean getRegisterBean(Class<Bean> bean) {
        return null;
    }

    @Override
    public Set<BeanInfo<?>> getRegisteredBeans() {
        return new HashSet<>();
    }

    @Override
    public Set<MutableBeanInfo<?>> getRegisteredRawBeans() {
        return new HashSet<>();
    }

    @Override
    public Set<Class<?>> getRegisteredRawBeanType() {
        return new HashSet<>();
    }

    @Override
    public <Bean> Set<Method> getBeanMethods(Class<Bean> beanClass) {
        return new HashSet<>();
    }

    @Override
    public void reset() {

    }
}
