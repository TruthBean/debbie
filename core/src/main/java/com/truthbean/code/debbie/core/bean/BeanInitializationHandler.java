package com.truthbean.code.debbie.core.bean;

import com.truthbean.code.debbie.core.reflection.ClassInfo;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:47.
 */
public class BeanInitializationHandler {
    public static void init(Class<?> beanClass) {
        BeanCacheHandler.register(beanClass);
    }

    public static void init(Set<Class<?>> beanClasses) {
        if (beanClasses != null && !beanClasses.isEmpty()) {
            beanClasses.forEach(BeanInitializationHandler::init);
        }
    }

    public void init(String packageName) {

    }

    public static <T extends Annotation> Set<ClassInfo> getAnnotatedClass(Class<T> annotationClass) {
        return BeanCacheHandler.getAnnotatedClass(annotationClass);
    }

    public static <T extends Annotation> Set<ClassInfo> getAnnotatedMethodBean(Class<T> annotationClass) {
        return BeanCacheHandler.getAnnotatedMethodsBean(annotationClass);
    }

    public static ClassInfo getRegisterBean(Class<?> bean) {
        return BeanCacheHandler.getRegisterBean(bean);
    }

}
