/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.proxy.*;
import com.truthbean.debbie.proxy.asm.AbstractProxy;
import com.truthbean.debbie.proxy.asm.AsmProxy;
import com.truthbean.debbie.proxy.javaassist.JavassistProxy;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:32.
 */
public class BeanFactoryHandler {

    private final Set<DebbieBeanInfo<?>> beanServiceInfoSet = Collections.synchronizedSet(new HashSet<>());

    private final Map<DebbieBeanInfo<?>, BeanInvoker<?>> singletonBeanInvokerMap = new ConcurrentHashMap<>();

    private final BeanInitialization beanInitialization;
    private final DebbieConfigurationFactory configurationFactory;
    private final MethodProxyHandlerRegister methodProxyHandlerRegister;

    private final ClassLoader classLoader;
    private final ResourceResolver resourceResolver;

    private Class<? extends Annotation> injectType;

    private final InjectedBeanFactory injectedBeanFactory;
    private final BeanDependenceProcessor beanDependenceProcessor;

    private static final Object object = new Object();

    @SuppressWarnings("unchecked")
    protected BeanFactoryHandler(ClassLoader classLoader) {
        synchronized (object) {
            resourceResolver = new ResourceResolver();
            beanInitialization = BeanInitialization.getInstance(classLoader, resourceResolver);
            configurationFactory = new DebbieConfigurationFactory(this);
            methodProxyHandlerRegister = new MethodProxyHandlerRegister();

            this.classLoader = classLoader;

            try {
                this.injectType = (Class<? extends Annotation>) Class.forName("javax.inject.Inject");
            } catch (ClassNotFoundException e) {
                LOGGER.info("class javax.inject.Inject not found");
            }

            this.injectedBeanFactory = new InjectedBeanFactory();
            this.injectedBeanFactory.setBeanFactoryHandler(this);

            this.beanDependenceProcessor = new BeanDependenceProcessor();
            this.beanDependenceProcessor.setBeanFactoryHandler(this);
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public Class<? extends Annotation> getInjectType() {
        return injectType;
    }

    public BeanInitialization getBeanInitialization() {
        return beanInitialization;
    }

    public DebbieConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    public MethodProxyHandlerRegister getMethodProxyHandlerRegister() {
        return methodProxyHandlerRegister;
    }

    public BeanDependenceProcessor getBeanDependenceProcessor() {
        return beanDependenceProcessor;
    }

    public InjectedBeanFactory getInjectedBeanFactory() {
        return injectedBeanFactory;
    }

    public void refreshBeans() {
        beanServiceInfoSet.addAll(beanInitialization.getRegisteredBeans());
        var beanServiceInfoList = beanInitialization.getAnnotatedBeans();

        beanServiceInfoList.forEach((i) -> {
            var clazz = i.getClazz();
            if (clazz.isAnnotation()) {
                @SuppressWarnings("unchecked") var annotation = (Class<? extends Annotation>) clazz;
                var set = beanInitialization.getAnnotatedClass(annotation);
                beanServiceInfoSet.addAll(set);

            } else {
                var beanFactory = i.getBeanFactory();
                if (beanFactory != null) {
                    beanServiceInfoSet.add(i);
                } else if (clazz.isInterface()) {
                    beanServiceInfoSet.addAll(beanInitialization.getBeanByInterface(clazz));
                } else if (Modifier.isAbstract(i.getClazz().getModifiers())) {
                    beanServiceInfoSet.addAll(beanInitialization.getBeanByAbstractSuper(clazz));
                } else {
                    beanServiceInfoSet.add(i);
                }
            }
        });

    }

    public void autoCreateSingletonBeans() {
        beanServiceInfoSet.forEach(i -> {
            Boolean lazyCreate = i.getLazyCreate();
            if (lazyCreate != null && !lazyCreate && i.getBeanType() == BeanType.SINGLETON) {
                i.setBean(factory(i.getServiceName()));
                beanInitialization.refreshBean(i);
            }
        });
    }

    Set<DebbieBeanInfo<?>> getAutoCreatedBean() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        for (DebbieBeanInfo<?> beanInfo : beanServiceInfoSet) {
            Boolean lazyCreate = beanInfo.getLazyCreate();
            if (lazyCreate != null && !lazyCreate) {
                result.add(beanInfo);
            }
        }
        return result;
    }

    public Set<DebbieBeanInfo<?>> getAllDebbieBeanInfo() {
        return Set.copyOf(beanServiceInfoSet);
    }

    public void destroy(DebbieBeanInfo<?> beanInfo) {
        beanServiceInfoSet.remove(beanInfo);
        singletonBeanInvokerMap.remove(beanInfo);
    }

    public void release(String... args) {
        // must do nothing
    }

    private synchronized void destroyBeans(Collection<DebbieBeanInfo<?>> beans) {
        if (beans != null && !beans.isEmpty()) {
            for (DebbieBeanInfo<?> bean : beans) {
                LOGGER.trace(() -> "release bean " + bean.getBeanClass() + " with name " + bean.getServiceName());
                bean.release();
            }
        }
    }

    protected void releaseBeans() {
        beanDependenceProcessor.destroy();
        injectedBeanFactory.destroy();

        destroyBeans(beanServiceInfoSet);
        destroyBeans(singletonBeanInvokerMap.keySet());
        beanServiceInfoSet.clear();
        singletonBeanInvokerMap.clear();
        beanInitialization.reset();
        resourceResolver.cleanResources();
        LOGGER.info("release all bean.");
    }

    private <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require,
                                              Set<DebbieBeanInfo<?>> beanInfoSet, boolean throwException) {
        List<DebbieBeanInfo<?>> list = new ArrayList<>();
        if (serviceName != null && !serviceName.isBlank()) {
            for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                if (serviceName.equals(debbieBeanInfo.getServiceName())) {
                    list.add(debbieBeanInfo);
                }
            }
        }

        if (list.isEmpty()) {
            if (type != null) {
                for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                    var flag = type.getName().equals(debbieBeanInfo.getBeanClass().getName())
                            || (debbieBeanInfo.getBeanInterface() != null
                            && type.getName().equals(debbieBeanInfo.getBeanInterface().getName()));
                    if (flag) {
                        list.add(debbieBeanInfo);
                    }
                }

                if (list.isEmpty()) {
                    for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                        var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                                || (debbieBeanInfo.getBeanInterface() != null
                                && type.isAssignableFrom(debbieBeanInfo.getBeanInterface()));
                        if (flag) {
                            list.add(debbieBeanInfo);
                        }
                    }
                }
            }

            if (list.size() == 0) {
                if (require) {
                    if ((serviceName == null || serviceName.isBlank()) && type != null) {
                        serviceName = type.getName();
                    }
                    if (throwException)
                        throw new NoBeanException(serviceName + " not found");
                    else
                        return null;
                } else {
                    return null;
                }
            }
        }

        if (list.size() > 1) {
            if ((serviceName == null || serviceName.isBlank()) && type != null) {
                serviceName = type.getName();
            }
            throw new OneMoreBeanRegisteredException(serviceName + " must be only one");
        }

        @SuppressWarnings("unchecked") DebbieBeanInfo<T> beanInfo = (DebbieBeanInfo<T>) list.get(0);
        if (type == null || type.isAssignableFrom(beanInfo.getBeanClass())) {
            if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                return beanInfo;
            } else {
                return beanInfo.copy();
            }
        }
        if (throwException)
            throw new NoBeanException("bean " + type + " not found");
        else
            return null;
    }

    private <T, K extends T> List<DebbieBeanInfo<K>> getBeanInfoList(Class<T> type, boolean require,
                                                                     Set<DebbieBeanInfo<?>> beanInfoSet) {
        List<DebbieBeanInfo<?>> list = new ArrayList<>();

        if (type != null) {
            for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                var flag = type.getName().equals(debbieBeanInfo.getBeanClass().getName())
                        || (debbieBeanInfo.getBeanInterface() != null
                        && type.getName().equals(debbieBeanInfo.getBeanInterface().getName()));
                if (flag) {
                    list.add(debbieBeanInfo);
                }
            }

            if (list.isEmpty()) {
                for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                    var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                            || (debbieBeanInfo.getBeanInterface() != null
                            && type.isAssignableFrom(debbieBeanInfo.getBeanInterface()));
                    if (flag) {
                        list.add(debbieBeanInfo);
                    }
                }
            }

            if (list.size() == 0) {
                if (require) {
                    throw new NoBeanException(type.getName() + " not found");
                } else {
                    return null;
                }
            }

            List<DebbieBeanInfo<K>> result = new ArrayList<>();
            for (DebbieBeanInfo<?> beanInfo : list) {
                if (type.isAssignableFrom(beanInfo.getBeanClass())) {
                    @SuppressWarnings("unchecked")
                    DebbieBeanInfo<K> ele = (DebbieBeanInfo<K>) beanInfo;
                    if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                        result.add(ele);
                    } else {
                        result.add(ele.copy());
                    }
                }
            }
            return result;
        }

        return null;
    }

    public <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require) {
        try {
            return getBeanInfo(serviceName, type, require, beanServiceInfoSet, true);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require, boolean throwException) {
        var beanInfo = getBeanInfo(serviceName, type, require, beanServiceInfoSet, throwException);
        if (!require && beanInfo == null)
            return null;
        assert beanInfo != null;
        return factory(beanInfo);
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require) {
        return factory(serviceName, type, require, true);
    }

    public synchronized <T> T factory(String serviceName) {
        LOGGER.trace(() -> "factory bean with name " + serviceName);
        return factory(serviceName, null, true);
    }

    private <T, K extends T> T factoryAfterCreatedByProxy(DebbieBeanInfo<K> beanInfo, BeanProxyType proxyType) {
        if (!beanInfo.hasAnnotatedMethod()) {
            return beanInfo.getBean();
        }
        Class<K> clazz = beanInfo.getBeanClass();
        Class<T> beanInterface = beanInfo.getBeanInterface();
        if (beanInterface != null) {
            LOGGER.trace(() -> "resolve field dependent bean(" + beanInterface + ") by implement class " + clazz);
            JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
            return dynamicProxy.invokeJdkProxy(this, beanInterface, beanInfo.getBean());
        }
        if (proxyType == BeanProxyType.NO) {
            return beanInfo.getBean();
        }
        if (!beanInfo.isMethodParameterContainPrimitiveClass()) {
            MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
            MethodProxyHandlerProcessor<K> processor =
                    new MethodProxyHandlerProcessor<>(this, handler, beanInfo).process();
            if (processor.hasNoProxy()) return beanInfo.getBean();

            try {
                AbstractProxy<K> proxy;
                if (!beanInfo.isMethodParameterMoreThanOne()) {
                    proxy = new AsmProxy<>(beanInfo.getBeanClass(), classLoader, handler, MethodProxy.class);
                } else {
                    proxy = new JavassistProxy<>(beanInfo.getBeanClass(), classLoader, handler, MethodProxy.class);
                }
                return processor.proxy(proxy);
            } catch (Exception e) {
                LOGGER.error("", e);
                return beanInfo.getBean();
            }
        } else {
            return beanInfo.getBean();
        }
    }

    public <T, K extends T> T factory(DebbieBeanInfo<K> beanInfo) {
        if (beanInfo.getBeanType() == BeanType.SINGLETON && beanInfo.isPresent()) {
            return beanInfo.getBean();
        }
        var beanFactory = beanInfo.getBeanFactory();
        if (beanFactory != null) {
            if (System.getSecurityManager() != null) {
                // TODO to set securityContextProvider
                try {
                    AccessControlContext securityContextProvider = AccessController.getContext();
                    AccessController.doPrivileged((PrivilegedAction<T>) () -> factoryBeanByFactory(beanInfo, beanFactory), securityContextProvider);
                } catch (Exception e) {
                    LOGGER.error("getBean from factory via securityContextProvider error", e);
                }
            } else
                return factoryBeanByFactory(beanInfo, beanFactory);
        }

        return factoryBeanByDependenceProcessor(beanInfo);
    }

    private <T> T factoryBeanByFactory(DebbieBeanInfo<T> beanInfo, BeanFactory<T> beanFactory) {
        T bean = beanFactory.factoryBean();
        beanInfo.setBean(bean);
        return factoryAfterCreatedByProxy(beanInfo, BeanProxyType.ASM);
    }

    public <T> T factoryBeanByDependenceProcessor(DebbieBeanInfo<T> beanInfo) {
        T bean = injectedBeanFactory.factory(beanInfo, beanDependenceProcessor);
        beanInfo.setBean(bean);
        return factoryAfterCreatedByProxy(beanInfo, BeanProxyType.ASM);
    }

    public <T> T factory(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName());
        return factory(null, type, true);
    }

    public <T> DebbieBeanInfo<T> getBeanInfo(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName());
        var beanInfo = getBeanInfo(null, type, true, beanServiceInfoSet, true);
        if (beanInfo != null) {
            T bean = factory(beanInfo);
            DebbieBeanInfo<T> result = new DebbieBeanInfo<>(beanInfo.getBeanClass());
            result.setBean(bean);
            return result;
        }
        return null;
    }

    public <T, K extends T> List<K> getBeanList(Class<T> superType) {
        List<K> result = new ArrayList<>();
        LOGGER.trace(() -> "factory bean with type " + superType.getName());
        List<DebbieBeanInfo<K>> beanInfoList = getBeanInfoList(superType, false, beanServiceInfoSet);
        if (beanInfoList != null) {
            for (DebbieBeanInfo<K> beanInfo : beanInfoList) {
                K bean = factory(beanInfo);
                result.add(bean);
            }
        }
        return result;
    }

    public <T> boolean containsBean(Class<T> beanType) {
        return getBeanInfoList(beanType, false, beanServiceInfoSet) != null;
    }

    public boolean containsBean(String beanName) {
        return getBeanInfo(beanName, null, false, beanServiceInfoSet, false) != null;
    }

    public <Bean> BeanInvoker<Bean> factoryBeanInvoker(Class<Bean> beanClass) {
        return new BeanInvoker<>(beanClass, this);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(BeanFactoryHandler.class);
}
