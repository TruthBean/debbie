package com.truthbean.debbie.core.bean;

import com.truthbean.debbie.core.reflection.ClassInfo;
import com.truthbean.debbie.core.spi.SpiLoader;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:47.
 */
public class BeanInitialization {
    static {
        // TODO: how to flush
        Set<AnnotationRegister> annotationRegisters = SpiLoader.loadProviders(AnnotationRegister.class);
        annotationRegisters.forEach(AnnotationRegister::register);
    }

    public void init(Class<?> beanClass) {
        BeanCacheHandler.register(beanClass);
    }

    public void init(DebbieBeanInfo<?> beanInfo) {
        BeanCacheHandler.register(beanInfo);
    }

    public void init(Set<Class<?>> beanClasses) {
        if (beanClasses != null && !beanClasses.isEmpty()) {
            beanClasses.forEach(this::init);
        }
    }

    public void init(String... packageName) {
        if (packageName != null) {
            for (String s : packageName) {
                BeanCacheHandler.register(s);
            }
        }
    }

    public <T extends Annotation> Set<DebbieBeanInfo> getAnnotatedClass(Class<T> annotationClass) {
        return BeanCacheHandler.getAnnotatedClass(annotationClass);
    }

    public <T extends Annotation> Set<DebbieBeanInfo> getAnnotatedMethodBean(Class<T> annotationClass) {
        return BeanCacheHandler.getAnnotatedMethodsBean(annotationClass);
    }

    public Set<DebbieBeanInfo> getBeanByInterface(Class<?> interfaceType) {
        return BeanCacheHandler.getBeansByInterface(interfaceType);
    }

    public Set<DebbieBeanInfo> getBeanByAbstractSuper(Class<?> abstractType) {
        return BeanCacheHandler.getBeansByInterface(abstractType);
    }

    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return BeanCacheHandler.getBeanAnnotations();
    }

    public <Bean> DebbieBeanInfo<Bean> getRegisteredBean(Class<Bean> bean) {
        return BeanCacheHandler.getRegisterBean(bean);
    }

}
