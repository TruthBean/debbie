package com.truthbean.debbie.bean;

import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.reflection.FieldInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public interface InjectedBeanFactory {
    <A extends Annotation> void registerInjectType(Class<A> injectType);

    void registerInjectType(Set<Class<? extends Annotation>> injectTypes);

    boolean containInjectType(Class<? extends Annotation> annotation);

    Set<Class<? extends Annotation>> getInjectTypes();

    Class<? extends Annotation> getInjectType();

    <T> T factory(DebbieBeanInfo<T> beanInfo);

    <T> T factory(DebbieBeanInfo<T> beanInfo, boolean skipFactory);

    <T> T factory(DebbieBeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue);

    <T> T factory(BeanCreator<T> creator);

    <T> T factory(BeanCreator<T> creator, Object firstParamValue);

    <T> BeanCreator<T> factoryBeanPreparation(DebbieBeanInfo<T> beanInfo, boolean skipFactory);

    void resolveFieldValue(DebbieBeanInfo<?> beanInfo, FieldInfo field, String keyPrefix);

    void resolveAwareValue(InjectedBeanFactory injectedBeanFactory, Object object, Class<?> clazz);

    void resolveMethodValue(Object object, Method method);

    Object factoryProperty(Class<?> valueType, String keyPrefix, PropertyInject propertyInject);

    void destroy();
}
