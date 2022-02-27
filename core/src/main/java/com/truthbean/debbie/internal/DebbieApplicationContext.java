/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.event.*;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.lang.Nullable;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.JdkBeanProxyHandler;
import com.truthbean.debbie.task.TaskFactory;
import com.truthbean.transformer.DataTransformerCenter;

import java.util.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:32.
 */
class DebbieApplicationContext implements ApplicationContext, GlobalBeanFactory {

    private final BeanInfoManager beanInfoManager;

    private final ClassLoader classLoader;
    private final ResourceResolver resourceResolver;

    private final ApplicationArgs applicationArgs;
    private final EnvironmentContent envContent;

    private static final Object OBJECT = new Object();

    private EventListenerBeanManager eventListenerBeanManager;

    private final AtomicBoolean exiting = new AtomicBoolean(false);

    protected DebbieApplicationContext(@Nullable Class<?> applicationClass, ClassLoader classLoader,
                                       ApplicationArgs applicationArgs, EnvironmentContent envContent,
                                       Class<?>... beanClasses) {
        exiting.set(false);
        synchronized (OBJECT) {
            this.applicationArgs = applicationArgs;
            this.envContent = envContent;
            this.resourceResolver = new ResourceResolver();
            this.beanInfoManager = doBeanInitialization(applicationClass, classLoader, resourceResolver, envContent);
            this.resourceResolver.addResource(beanClasses);
            this.resourceResolver.addResource(applicationClass);

            this.classLoader = classLoader;
        }
    }

    private BeanInfoManager doBeanInitialization(@Nullable Class<?> applicationClass, ClassLoader classLoader,
                                                 ResourceResolver resourceResolver, EnvironmentContent envContent) {
        if (envContent.getBooleanValue(ClassesScanProperties.RESOURCE_SCAN_ENABLE_KEY, true)) {
            List<String> resources = ResourcesHandler.getAllClassPathResources("", classLoader);
            resourceResolver.addResource(classLoader, resources);
            resources = ResourcesHandler.getAllClassPathResources(".", classLoader);
            resourceResolver.addResource(classLoader, resources);
            resources = ResourcesHandler.getAllClassPathResources("com/truthbean", classLoader);
            resourceResolver.addResource(classLoader, resources);
            if (applicationClass != null) {
                resources = ResourcesHandler.getAllClassPathResources(applicationClass.getPackageName().replace(".", "/"), classLoader);
                resourceResolver.addResource(classLoader, resources);
            }
        }
        DebbieBeanCenter debbieBeanCenter = new DebbieBeanCenter(envContent);
        debbieBeanCenter.registerBeanRegister(new ConfigurationBeanRegister(this));
        debbieBeanCenter.registerBeanAnnotation(BeanComponent.class, new DefaultBeanComponentParser());
        return debbieBeanCenter;
    }

    protected void postConstructor() {
        final JdkBeanProxyHandler beanProxyHandler = new JdkBeanProxyHandler(this);

        this.beanInfoManager.registerBeanLifecycle(new SimpleBeanLifecycle(this, beanProxyHandler));
        this.beanInfoManager.registerBeanLifecycle(new ReflectionBeanLifecycle(beanProxyHandler, this));
    }

    private volatile AutoCreatedBeanFactory autoCreatedBeanFactory;

    synchronized void postCallStarter(DebbieApplication application) {
        // create not lazy beans
        if (this.autoCreatedBeanFactory == null) {
            autoCreatedBeanFactory = new AutoCreatedBeanFactory(application);
        }
        autoCreatedBeanFactory.autoCreateBeans(this, beanInfoManager);
        // do startedEvent
        multicastDebbieStartedEvent(this);
    }

    public void setEventListenerBeanManager(EventListenerBeanManager eventListenerBeanManager) {
        this.eventListenerBeanManager = eventListenerBeanManager;
    }

    private volatile DebbieStartedEventProcessor processor;

    public void multicastDebbieStartedEvent(ApplicationContext applicationContext) {
        if (this.processor == null) {
            this.processor = new DebbieStartedEventProcessor(applicationContext);
        }
        processor.multicastDebbieStartedEvent();
    }

    synchronized void beforeRelease() {
        if (this.autoCreatedBeanFactory != null) {
            this.autoCreatedBeanFactory.stopAll();
        }
        if (this.processor != null) {
            processor.stopAll();
        }
    }

    @Override
    public ApplicationArgs getApplicationArgs() {
        return applicationArgs;
    }

    @Override
    public EnvironmentContent getEnvContent() {
        return envContent;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    @Override
    public BeanInfoManager getBeanInfoManager() {
        return beanInfoManager;
    }

    @Override
    public GlobalBeanFactory getGlobalBeanFactory() {
        return this;
    }

    @Override
    public Set<BeanLifecycle> getBeanLifecycle() {
        return beanInfoManager.getBeanLifecycles();
    }

    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
        eventListenerBeanManager.publishEvent(event);
    }

    @Override
    public <T extends I, I> void registerSingleBean(Class<I> beanClass, T bean, String... names) {
        beanInfoManager.registerBeanInfo(new SimpleBeanFactory<>(bean, beanClass, BeanProxyType.JDK, names));
    }

    @Override
    public void registerBeanLifecycle(BeanLifecycle beanLifecycle) {
        beanInfoManager.registerBeanLifecycle(beanLifecycle);
    }

    /*@Override
    public void refreshBeans() {
        this.beanInfoManager.refreshBeans();
    }*/

    @Override
    public <O, T> T transform(final O origin, final Class<T> target) {
        return DataTransformerCenter.transform(origin, target);
    }

    @Override
    public synchronized <T> T factory(String beanName) {
        LOGGER.trace(() -> "factory bean with name " + beanName);
        return factory(beanName, null, false, true, true);
    }

    @Override
    public <T> T factory(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName());
        return factory(null, type,  true, true, true);
    }

    @Override
    public <T> T factoryWithoutProxy(Class<T> type) {
        return factory(null, type, false, true, true);
    }

    @Override
    public <T> T factoryIfPresent(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName() + " if present");
        return factory(null, type, true, false, false);
    }

    @Override
    public <T> T factoryIfPresentOrElse(Class<T> type, Supplier<T> otherFactory) {
        LOGGER.trace(() -> "factory bean with type " + type.getName() + " if present");
        T bean = factory(null, type, true, false, false);
        if (bean == null && otherFactory != null) {
            return otherFactory.get();
        }
        return bean;
    }

    @Override
    public <T> Optional<T> factoryIfPresent(String beanName) {
        LOGGER.trace(() -> "factory bean with name " + beanName + " if present");
        return Optional.ofNullable(factory(beanName, null, false, false, false));
    }

    @Override
    public <T> Supplier<T> supply(String beanName) {
        return () -> factory(beanName, null, false, true, false);
    }

    @Override
    public <T> Supplier<T> supply(Class<T> type) {
        return () -> factory(null, type, true, true, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void factoryByRawBean(T rawBean) {
        var debbieBeanInfo = new DebbieReflectionBeanFactory<>((Class<T>) rawBean.getClass(), rawBean);
        debbieBeanInfo.factoryBean(this);
    }

    @Override
    public <T> T factoryByNoBean(Class<T> noBeanType) {
        synchronized (beanInfoManager) {
            var beanInfo = this.beanInfoManager.getBeanFactory(null, noBeanType, false);
            var beanFactory = Objects.requireNonNullElseGet(beanInfo, () -> new DebbieReflectionBeanFactory<>(noBeanType));
            return beanFactory.factoryBean(this);
        }
    }

    // @SuppressWarnings("unchecked")
    protected <T> T factory(String beanName, Class<T> type, boolean proxy, boolean require, boolean throwException) {
        /*if (beanName != null && type != null && DebbieConfiguration.class.isAssignableFrom(type)) {
            DebbieConfiguration configuration = this.factory(beanName, (Class<? extends DebbieConfiguration>) type, false);
            if (configuration != null) {
                return (T) configuration;
            }
        }*/
        synchronized (beanInfoManager) {
            var beanFactory = this.beanInfoManager.getBeanFactory(beanName, type, require);
            if (!require && beanFactory == null) {
                return null;
            }
            if (throwException && beanFactory == null) {
                throw new BeanCreatedException("bean " + type + "(" + beanName + ") has no factory");
            }
            T bean;
            if (proxy) {
                bean = beanFactory.factoryProxiedBean(beanName, type, this);
            } else {
                bean = beanFactory.factoryNamedBean(beanName, this);
            }
            if (throwException && bean == null) {
                throw new BeanCreatedException("create bean " + type + " with name [" + beanName + "]) error");
            }
            return bean;
        }
    }

    @Override
    public <T> T factory(String beanName, Class<T> type, boolean require) {
        return factory(beanName, type, true, require, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Bean> Set<Bean> getBeanList(Class<Bean> superType) {
        synchronized (beanInfoManager) {
            Set<Bean> result = new HashSet<>();
            LOGGER.trace(() -> "factory bean with type " + superType.getName());
            List<BeanInfo<? extends Bean>> beanInfoList = this.beanInfoManager.getBeanInfoList(superType, false);
            if (beanInfoList != null && !beanInfoList.isEmpty()) {
                for (BeanInfo<? extends Bean> beanInfo : beanInfoList) {
                    Set<String> beanNames = beanInfo.getBeanNames();
                    if (beanInfo instanceof BeanFactory) {
                        if (beanNames != null && !beanNames.isEmpty()) {
                            for (String beanName : beanNames) {
                                Bean bean = ((BeanFactory<? extends Bean>) beanInfo).factoryProxiedBean(beanName, superType, this);
                                if (bean == null) {
                                    throw new BeanCreatedException("factory bean (" + beanName + ", " + superType + ") error");
                                }
                                result.add(bean);
                            }
                        } else {
                            Bean bean = ((BeanFactory<? extends Bean>) beanInfo).factoryBean(this);
                            if (bean == null) {
                                throw new BeanCreatedException("factory bean (" + superType + ") error");
                            }
                            result.add(bean);
                        }
                    }
                }
            }
            return result;
        }
    }

    @Override
    public <T> boolean containsBean(Class<T> beanType) {
        synchronized (beanInfoManager) {
            return this.beanInfoManager.getBeanInfoList(beanType, false) != null;
        }
    }

    @Override
    public boolean containsBean(String beanName) {
        synchronized (beanInfoManager) {
            return this.beanInfoManager.getBeanInfo(beanName, null, false, false) != null;
        }
    }

    @Override
    public boolean isExiting() {
        return exiting.get();
    }

    @Override
    public void release(String... args) {
        LOGGER.info("release resource before application shutdown.");
        exiting.set(true);
        Optional<ThreadPooledExecutor> executor = this.factoryIfPresent("threadPooledExecutor");
        executor.ifPresent(ThreadPooledExecutor::destroy);
        Optional<TaskFactory> taskFactory = this.factoryIfPresent("taskFactory");
        taskFactory.ifPresent(factory -> factory.destruct(this));
    }

    protected void releaseBeans() {
        synchronized (DebbieApplicationContext.class) {
            this.beanInfoManager.reset(this);
            resourceResolver.cleanResources();
            DataTransformerCenter.reset();
            LOGGER.info("destruct all bean.");
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationContext.class);
}
