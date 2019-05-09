package com.truthbean.debbie.core.bean;

import com.truthbean.debbie.core.reflection.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 12:05.
 */
public class BeanInvoker<Bean> {
    private final Class<Bean> beanClass;
    private static final Map<String, Method> BEAN_METHODS = new HashMap<>();

    private Bean bean;
    private ClassInfo classInfo;

    public BeanInvoker(Class<Bean> beanClass) {
        this.beanClass = beanClass;
        var methods = BeanCacheHandler.getBeanMethods(beanClass);
        this.classInfo = BeanCacheHandler.getRegisterBean(beanClass);
        methods.forEach(method -> BEAN_METHODS.put(method.getName(), method));
        createBean();
    }

    // TODO cache
    private void createBean() {
        try {
            var constructor = beanClass.getConstructor();
            this.bean = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("new instance by default constructor error", e);
        }
    }

    public Bean getBean() {
        return bean;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public Object invokeMethod(String methodName, Object[] parameters) {
        var beanMethod = BEAN_METHODS.get(methodName);
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Class<? extends Annotation> methodAnnotation, String methodName, Object[] parameters) {
        var beanMethod = BEAN_METHODS.get(methodName);
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Method routerMethod, Object[] parameters) {
        try {
            if (parameters == null || parameters.length == 0) {
                return routerMethod.invoke(bean);
            } else {
                return routerMethod.invoke(bean, parameters);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("invokeMethod error", e);
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanInvoker.class);
}
