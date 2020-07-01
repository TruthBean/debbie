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
import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:47.
 */
public class BeanInitialization {
    private final BeanRegisterCenter beanRegisterCenter;
    private final BeanConfigurationRegister beanConfigurationRegister;
    private BeanInitialization(ClassLoader classLoader, ResourceResolver resourceResolver) {
        List<String> resources = ResourcesHandler.getAllClassPathResources("", classLoader);
        resourceResolver.addResource(resources);
        beanRegisterCenter = new BeanRegisterCenter();
        beanRegisterCenter.registerBeanAnnotation(BeanComponent.class);
        beanConfigurationRegister = new BeanConfigurationRegister(beanRegisterCenter, resourceResolver);
    }

    private static BeanInitialization initialization;
    private static ResourceResolver resourceResolver;

    static BeanInitialization getInstance(ClassLoader classLoader, ResourceResolver resourceResolver) {
        if (initialization == null) {
            synchronized (BeanInitialization.class) {
                if (initialization == null) {
                    initialization = new BeanInitialization(classLoader, resourceResolver);
                    BeanInitialization.resourceResolver = resourceResolver;
                }
            }

        }
        return initialization;
    }

    <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType) {
        beanRegisterCenter.registerBeanAnnotation(annotationType);
    }

    private static final Set<AnnotationRegister<?>> annotationRegisters = new HashSet<>();
    public <A extends AnnotationRegister<?>> void addAnnotationRegister(A register) {
        annotationRegisters.add(register);
        register.register();
    }
    public void registerAnnotations() {
        for (AnnotationRegister<?> annotationRegister : annotationRegisters) {
            annotationRegister.register();
        }
    }

    public void registerBeanConfiguration(Collection<Class<?>> classes) {
        beanConfigurationRegister.register(classes);
    }

    public <D extends DataTransformer<?,?>> void registerDataTransformer(D dataTransformer, Type argsType1, Type argsType2) {
        Type[] types = new Type[2];
        types[0] = argsType1;
        types[1] = argsType2;
        DataTransformerFactory.register(dataTransformer, types);
    }
    public <D extends DataTransformer<?,?>> void registerDataTransformer(D transformer) {
        Type[] argsType = ReflectionHelper.getActualTypes(transformer.getClass());
        DataTransformerFactory.register(transformer, argsType);
    }
    public <O, T> T transform(final O origin, final Class<T> target) {
        return DataTransformerFactory.transform(origin, target);
    }

    public void init(Class<?> beanClass) {
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

    public void initBean(DebbieBeanInfo<?> beanInfo) {
        beanRegisterCenter.register(beanInfo);
    }

    public void initSingletonBean(DebbieBeanInfo<?> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanRegisterCenter.register(beanInfo);
    }

    public void refreshSingletonBean(DebbieBeanInfo<?> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanRegisterCenter.refresh(beanInfo);
    }

    public void refreshBean(DebbieBeanInfo<?> beanInfo) {
        beanRegisterCenter.refresh(beanInfo);
    }

    public void init(Set<Class<?>> beanClasses) {
        if (beanClasses != null && !beanClasses.isEmpty()) {
            beanClasses.forEach(this::init);
        }
    }

    public void init(ClassLoader classLoader, String... packageName) {
        if (packageName != null) {
            for (String s : packageName) {
                beanRegisterCenter.register(s, classLoader, resourceResolver);
            }
        }
    }

    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        return beanRegisterCenter.getAnnotatedClass(annotationClass);
    }

    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedBeans() {
        return beanRegisterCenter.getAnnotatedBeans();
    }

    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedMethodBean(Class<T> annotationClass) {
        return beanRegisterCenter.getAnnotatedMethodsBean(annotationClass);
    }

    public Set<DebbieBeanInfo<?>> getBeanByInterface(Class<?> interfaceType) {
        return beanRegisterCenter.getBeansByInterface(interfaceType);
    }

    public Set<DebbieBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
        return beanRegisterCenter.getBeansByInterface(abstractType);
    }

    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return beanRegisterCenter.getBeanAnnotations();
    }

    public <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        return beanRegisterCenter.getRegisterRawBean(bean);
    }

    public <Bean> Bean getRegisterBean(Class<Bean> bean) {
        DebbieBeanInfo<Bean> registerRawBean = beanRegisterCenter.getRegisterRawBean(bean);
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

    public Set<DebbieBeanInfo<?>> getRegisteredBeans() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        Collection<DebbieBeanInfo<?>> registerRawBeans = beanRegisterCenter.getRegisterRawBeans();
        if (!registerRawBeans.isEmpty()) {
            for (DebbieBeanInfo<?> registerRawBean : registerRawBeans) {
                if (registerRawBean.isPresent() || registerRawBean.getBeanFactory() != null) {
                    result.add(registerRawBean);
                }
            }
        }
        return result;
    }

    public Set<DebbieBeanInfo<?>> getRegisteredRawBeans() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        Collection<DebbieBeanInfo<?>> registerRawBeans = beanRegisterCenter.getRegisterRawBeans();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }

    public Set<Class<?>> getRegisteredRawBeanType() {
        Set<Class<?>> result = new HashSet<>();
        Collection<Class<?>> registerRawBeans = beanRegisterCenter.getRegisterRawBeanTypes();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }

    public <Bean> Set<Method> getBeanMethods(Class<Bean> beanClass) {
        return beanRegisterCenter.getBeanMethods(beanClass);
    }

    public void reset() {
        annotationRegisters.clear();
        beanRegisterCenter.reset();
    }

}
