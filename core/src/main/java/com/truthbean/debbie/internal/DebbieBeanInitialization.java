/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.lang.Nullable;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author TruthBean
 * @since 0.1.0
 */
class DebbieBeanInitialization implements BeanInitialization {
    private final BeanRegisterCenter beanRegisterCenter;
    private final BeanConfigurationRegister beanConfigurationRegister;
    private DebbieBeanInitialization(@Nullable Class<?> applicationClass, ClassLoader classLoader, ResourceResolver resourceResolver) {
        List<String> resources = ResourcesHandler.getAllClassPathResources("", classLoader);
        resourceResolver.addResource(resources);
        resources = ResourcesHandler.getAllClassPathResources(".", classLoader);
        resourceResolver.addResource(resources);
        resources = ResourcesHandler.getAllClassPathResources("com/truthbean", classLoader);
        resourceResolver.addResource(resources);
        if (applicationClass != null) {
            resources = ResourcesHandler.getAllClassPathResources(applicationClass.getPackageName().replace(".", "/"), classLoader);
            resourceResolver.addResource(resources);
        }
        beanRegisterCenter = new BeanRegisterCenter();
        beanRegisterCenter.registerBeanAnnotation(BeanComponent.class, new DefaultBeanComponentParser());
        beanConfigurationRegister = new BeanConfigurationRegister(beanRegisterCenter, resourceResolver);
    }

    private static BeanInitialization initialization;
    private static ResourceResolver resourceResolver;

    static BeanInitialization getInstance(@Nullable Class<?> applicationClass, ClassLoader classLoader, ResourceResolver resourceResolver) {
        if (initialization == null) {
            synchronized (BeanInitialization.class) {
                if (initialization == null) {
                    initialization = new DebbieBeanInitialization(applicationClass, classLoader, resourceResolver);
                    DebbieBeanInitialization.resourceResolver = resourceResolver;
                }
            }

        }
        return initialization;
    }

    /**
     * @param <A> annotation type
     * @param annotationType registered by
     *      @see BeanComponent
     * 标识的 annotation
     */
    @Override
    public <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser) {
        beanRegisterCenter.registerBeanAnnotation(annotationType, parser);
    }

    @Override
    public void registerBeanConfiguration(Collection<Class<?>> classes) {
        beanConfigurationRegister.register(classes);
    }

    @Override
    public <D extends DataTransformer<?,?>> void registerDataTransformer(D dataTransformer, Type argsType1, Type argsType2) {
        Type[] types = new Type[2];
        types[0] = argsType1;
        types[1] = argsType2;
        DataTransformerFactory.register(dataTransformer, types);
    }
    @Override
    public <D extends DataTransformer<?,?>> void registerDataTransformer(D transformer) {
        Type[] argsType = ReflectionHelper.getActualTypes(transformer.getClass());
        DataTransformerFactory.register(transformer, argsType);
    }
    @Override
    public <O, T> T transform(final O origin, final Class<T> target) {
        return DataTransformerFactory.transform(origin, target);
    }

    @Override
    public synchronized void init(Class<?> beanClass) {
        if (beanClass.isAnonymousClass()) {
            if ("".equals(beanClass.getSimpleName())) {
                Class<?>[] interfaces = beanClass.getInterfaces();
                if (interfaces.length == 1) {
                    beanRegisterCenter.register(interfaces[0]);
                } else {
                    Class<?> superclass = beanClass.getSuperclass();
                    if (superclass != Object.class) {
                        beanRegisterCenter.register(superclass);
                    }
                }
            }
        } else {
            beanRegisterCenter.register(beanClass);
        }
    }

    @Override
    public void initBean(BeanInfo<?> beanInfo) {
        DebbieBeanInfo<?> debbieBeanInfo;
        if (beanInfo instanceof DebbieBeanInfo) {
            debbieBeanInfo = (DebbieBeanInfo<?>) beanInfo;
        } else {
            debbieBeanInfo = new DebbieBeanInfo<>(beanInfo);
        }
        beanRegisterCenter.register(debbieBeanInfo);
    }

    @Override
    public void initSingletonBean(DebbieBeanInfo<?> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanRegisterCenter.register(beanInfo);
    }

    @Override
    public void refreshSingletonBean(MutableBeanInfo<?> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanRegisterCenter.refresh(beanInfo);
    }

    @Override
    public void refreshBean(MutableBeanInfo<?> beanInfo) {
        beanRegisterCenter.refresh(beanInfo);
    }

    @Override
    public synchronized void init(final Set<Class<?>> beanClasses) {
        if (beanClasses != null && !beanClasses.isEmpty()) {
            beanClasses.forEach(this::init);
        }
    }

    @Override
    public void init(ClassLoader classLoader, String... packageName) {
        if (packageName != null) {
            for (String s : packageName) {
                beanRegisterCenter.register(s, classLoader, resourceResolver);
            }
        }
    }

    @Override
    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        return beanRegisterCenter.getAnnotatedClass(annotationClass);
    }

    @Override
    public Set<DebbieBeanInfo<?>> getAnnotatedBeans() {
        return beanRegisterCenter.getAnnotatedBeans();
    }

    @Override
    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedMethodBean(Class<T> annotationClass) {
        return beanRegisterCenter.getAnnotatedMethodsBean(annotationClass);
    }

    @Override
    public Set<DebbieBeanInfo<?>> getBeanByInterface(Class<?> interfaceType) {
        return beanRegisterCenter.getBeansByInterface(interfaceType);
    }

    @Override
    public Set<DebbieBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
        return beanRegisterCenter.getBeansByInterface(abstractType);
    }

    @Override
    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return beanRegisterCenter.getBeanAnnotations();
    }

    @Override
    public <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        return beanRegisterCenter.getRegisterRawBean(bean);
    }

    @Override
    public <Bean> Bean getRegisterBean(Class<Bean> bean) {
        MutableBeanInfo<Bean> registerRawBean = beanRegisterCenter.getRegisterRawBean(bean);
        if (registerRawBean == null) {
            return null;
        }
        Bean resultBean = registerRawBean.getBean();
        BeanFactory<Bean> beanFactory = registerRawBean.getBeanFactory();
        if (resultBean != null) {
            return resultBean;
        } else if (beanFactory != null) {
            return beanFactory.getBean();
        } else {
            throw new NoBeanException("bean " + bean.getName() + " has no registered! ");
        }
    }

    @Override
    public Set<DebbieBeanInfo<?>> getRegisteredBeans() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        Collection<DebbieBeanInfo<?>> registerRawBeans = new CopyOnWriteArrayList<>(beanRegisterCenter.getRegisterRawBeans());
        if (!registerRawBeans.isEmpty()) {
            for (DebbieBeanInfo<?> registerRawBean : registerRawBeans) {
                if (registerRawBean.isPresent() || registerRawBean.getBeanFactory() != null) {
                    result.add(registerRawBean);
                }
            }
        }
        return result;
    }

    @Override
    public Set<DebbieBeanInfo<?>> getRegisteredRawBeans() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        Collection<DebbieBeanInfo<?>> registerRawBeans = beanRegisterCenter.getRegisterRawBeans();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }

    @Override
    public Set<Class<?>> getRegisteredRawBeanType() {
        Set<Class<?>> result = new HashSet<>();
        Collection<Class<?>> registerRawBeans = beanRegisterCenter.getRegisterRawBeanTypes();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }

    @Override
    public <Bean> Set<Method> getBeanMethods(Class<Bean> beanClass) {
        return beanRegisterCenter.getBeanMethods(beanClass);
    }

    @Override
    public void reset() {
        beanRegisterCenter.reset();
        if (resourceResolver != null)
            resourceResolver.cleanResources();
        initialization = null;
        resourceResolver = null;
    }
}
