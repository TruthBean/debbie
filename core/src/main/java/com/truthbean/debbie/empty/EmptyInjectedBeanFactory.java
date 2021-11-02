package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.BeanCreator;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.InjectedBeanFactory;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.reflection.FieldInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 */
class EmptyInjectedBeanFactory implements InjectedBeanFactory {
    @Override
    public <A extends Annotation> void registerInjectType(Class<A> injectType) {

    }

    @Override
    public void registerInjectType(Set<Class<? extends Annotation>> injectTypes) {

    }

    @Override
    public boolean containInjectType(Class<? extends Annotation> annotation) {
        return false;
    }

    @Override
    public Set<Class<? extends Annotation>> getInjectTypes() {
        return new HashSet<>();
    }

    @Override
    public Class<? extends Annotation> getInjectType() {
        return null;
    }

    @Override
    public <T> T factory(BeanInfo<T> beanInfo) {
        return null;
    }

    @Override
    public <T> T factory(BeanInfo<T> beanInfo, boolean skipFactory) {
        return null;
    }

    @Override
    public <T> T factory(BeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue) {
        return null;
    }

    @Override
    public <T> T factory(BeanCreator<T> creator) {
        return null;
    }

    @Override
    public <T> T factory(BeanCreator<T> creator, Object firstParamValue) {
        return null;
    }

    @Override
    public <T> BeanCreator<T> factoryBeanPreparation(BeanInfo<T> beanInfo, boolean skipFactory) {
        return null;
    }

    @Override
    public void resolveFieldValue(BeanInfo<?> beanInfo, FieldInfo field, String keyPrefix) {

    }

    @Override
    public void resolveAwareValue(InjectedBeanFactory injectedBeanFactory, Object object, Class<?> clazz) {

    }

    @Override
    public void resolveMethodValue(Object object, Method method) {

    }

    @Override
    public Object factoryProperty(Class<?> valueType, String keyPrefix, PropertyInject propertyInject) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
