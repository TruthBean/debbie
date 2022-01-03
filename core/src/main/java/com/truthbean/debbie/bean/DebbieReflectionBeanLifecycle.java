/*
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

*/
/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 01:41.
 *//*

public class DebbieReflectionBeanLifecycle implements ReflectionBeanLifecycle {

    private final ApplicationContext applicationContext;
    private final BeanInfoManager beanInfoManager;

    public DebbieReflectionBeanLifecycle(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanInfoManager = applicationContext.getBeanInfoManager();
    }

    final Map<BeanInfo<?>, DebbieReflectionBeanFactory<?>> singletonBeanFactoryMap = new ConcurrentHashMap<>();
    private final Map<BeanInfo<?>, DebbieReflectionBeanFactory<?>> preparations = new LinkedHashMap<>();

    @Override
    public boolean support(Class<?> clazz) {
        return ReflectionBeanLifecycle.super.support(clazz);
    }

    @Override
    public <Bean> BeanInfo getBean(Class<Bean> clazz, String name) {
        return ReflectionBeanLifecycle.super.getBean(clazz, name);
    }

    @Override
    public void postConstruct(Object bean) {
        ReflectionBeanLifecycle.super.postConstruct(bean);
    }

    @Override
    public <T, K extends T> T doPreCreated(K bean, Class<T> clazz, BeanProxyType proxyType) {
        return ReflectionBeanLifecycle.super.doPreCreated(bean, clazz, proxyType);
    }

    @Override
    public <Bean> Bean getCreatedBean(Bean bean) {
        return ReflectionBeanLifecycle.super.getCreatedBean(bean);
    }

    @Override
    public void doBeforeDestruct(Object bean) {
        ReflectionBeanLifecycle.super.doBeforeDestruct(bean);
    }

    @Override
    public void destruct(Object bean) {
        ReflectionBeanLifecycle.super.destruct(bean);
    }

    @Override
    public <Bean> Bean getCreatePreparation(Executable initExecutable, Object firstParamValue, List<BeanExecutableDependence> dependences) {
        return null;
    }

    @Override
    public <Bean> void createPreparationByDependence(DebbieReflectionBeanFactory<Bean> beanFactory) {

    }

    @Override
    public void getPostConstructPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> beanCreatorMap) {

    }

    @Override
    public void getBeforeDestructPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> beanCreatorMap) {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieReflectionBeanLifecycle.class);
}
*/
