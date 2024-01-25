/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.event.*;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.lang.Nullable;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.JdkBeanProxyHandler;
import com.truthbean.debbie.task.TaskFactory;
import com.truthbean.transformer.DataTransformerCenter;

import java.lang.reflect.Proxy;
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

    private final Environment defaultEnvironment;
    private final EnvironmentDepositoryHolder environmentDepositoryHolder;

    private static final Object OBJECT = new Object();

    private EventListenerBeanManager eventListenerBeanManager;

    private final AtomicBoolean exiting = new AtomicBoolean(false);

    protected DebbieApplicationContext(@Nullable Class<?> applicationClass, ClassLoader classLoader,
                                       ApplicationArgs applicationArgs, EnvironmentDepositoryHolder environmentDepositoryHolder,
                                       Class<?>... beanClasses) {
        exiting.set(false);
        synchronized (OBJECT) {
            this.applicationArgs = applicationArgs;
            this.environmentDepositoryHolder = environmentDepositoryHolder;
            this.resourceResolver = new ResourceResolver();
            this.defaultEnvironment = environmentDepositoryHolder.getEnvironmentIfPresent(environmentDepositoryHolder.getDefaultProfile());
            this.beanInfoManager = doBeanInitialization(applicationClass, classLoader, resourceResolver, defaultEnvironment);
            this.resourceResolver.addResource(beanClasses);
            this.resourceResolver.addResource(applicationClass);

            this.classLoader = classLoader;
        }
    }

    private BeanInfoManager doBeanInitialization(@Nullable Class<?> applicationClass, ClassLoader classLoader,
                                                 ResourceResolver resourceResolver, Environment environment) {
        if (environment.getBooleanValue(ClassesScanProperties.RESOURCE_SCAN_ENABLE_KEY, true)) {
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
        DebbieBeanCenter debbieBeanCenter = new DebbieBeanCenter(environment);
        SimpleBeanFactory<DebbieApplicationContext, DebbieApplicationContext> applicationContextBeanFactory = new SimpleBeanFactory<>(this);
        debbieBeanCenter.registerBeanInfo(applicationContextBeanFactory);
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
    public Environment getDefaultEnvironment() {
        return defaultEnvironment;
    }

    @Override
    public EnvironmentDepositoryHolder getEnvironmentHolder() {
        return environmentDepositoryHolder;
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

    /*@Override
    public void refreshBeans() {
        this.beanInfoManager.refreshBeans();
    }*/

    @Override
    public <O, T> T transform(final O origin, final Class<T> target) {
        return DataTransformerCenter.transform(origin, target);
    }

    @Override
    public <T> T factory(String beanName) {
        LOGGER.trace(() -> "factory bean with name " + beanName);
        return factory(beanName, null, false, true, true);
    }

    @Override
    public <T> T factory(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName());
        return factory(null, type, true, true, true);
    }

    @Override
    public <T> List<T> factories(Class<T> beanType) {
        // todo
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T factoryConfiguration(Class<T> type, String profile, String category) {
        if (type != null && DebbieConfiguration.class.isAssignableFrom(type)) {
            BeanFactory<T> beanFactory = beanInfoManager.getBeanFactory(null, type, true, true);
            if (beanFactory instanceof PropertiesConfigurationBeanFactory<?, ?> propertiesConfigurationBeanFactory) {
                return (T) propertiesConfigurationBeanFactory.factory(profile, category, this);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T factory(BeanInjection<T> injection) {
        synchronized (beanInfoManager) {
            var beanInfoList = this.beanInfoManager.getBeanInfoList(injection);
            if (!injection.isRequire() && (beanInfoList == null || beanInfoList.isEmpty())) {
                return null;
            }
            if (injection.isThrowException() && (beanInfoList == null || beanInfoList.isEmpty())) {
                throw new BeanCreatedException("bean " + injection.getBeanClass() + "(" + injection.getBeanName() + ") has no factory");
            }
            T bean = null;
            for (BeanInfo beanInfo : beanInfoList) {
                if (beanInfo instanceof PropertiesConfigurationBeanFactory propertiesConfigurationBeanFactory) {
                    bean = (T) propertiesConfigurationBeanFactory.factory(injection, this);
                } else if (beanInfo instanceof BeanFactory<?> beanFactory) {
                    bean = (T) beanFactory.factoryBean(this);
                } else {
                    bean = (T) beanInfo.supply(this).get();
                }
                if (bean != null) {
                    break;
                }
            }
            if (injection.isThrowException() && bean == null) {
                throw new BeanCreatedException("create bean " + injection.getBeanClass() + " with name [" + injection.getBeanName() + "]) error");
            }
            return bean;
        }
    }

    @Override
    public <T> T factoryByRawBean(BeanInjection<T> injection, T rawBean) {
        T localBean = rawBean;
        Class<T> beanClass = injection.getBeanClass();
        if (!(rawBean instanceof Proxy)) {
            Set<BeanLifecycle> beanLifecycles = beanInfoManager.getBeanLifecycles();
            for (BeanLifecycle beanLifecycle : beanLifecycles) {
                if (beanLifecycle.support(beanClass)) {
                    localBean = beanLifecycle.construct(rawBean);
                    localBean = beanLifecycle.postConstruct(localBean);
                    localBean = beanLifecycle.doPreCreated(null, localBean, beanClass, injection.getProxyType());
                    if (localBean instanceof Proxy) {
                        return localBean;
                    }
                }
            }
        }
        return localBean;
    }

    @Override
    public <T> T factory(BeanInjection<T> injection, BeanSupplier<T> beanInfo) {
        if (beanInfo instanceof BeanFactory<T> beanFactory) {
            // todo get bean by beanName, type, resource
            // factoryProxiedBean(injection.getBeanClass(), injection.getProxyType(), beanFactory);
            // todo do proxy
        }

        Supplier<T> supplier = beanInfo.supply(this);
        return null;
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
            var beanInfo = this.beanInfoManager.getBeanFactory(null, noBeanType, false, false);
            var beanFactory = Objects.requireNonNullElseGet(beanInfo, () -> new DebbieReflectionBeanFactory<>(noBeanType));
            return beanFactory.factoryBean(this);
        }
    }

    // @SuppressWarnings("unchecked")
    protected <T> T factory(String beanName, Class<T> type, boolean proxy, boolean require, boolean throwException) {
        synchronized (beanInfoManager) {
            var beanInfo = this.beanInfoManager.getBeanInfo(beanName, type, require);
            if (!require && beanInfo == null) {
                return null;
            }
            if (throwException && beanInfo == null) {
                throw new BeanCreatedException("bean " + type + "(" + beanName + ") isn't registered to debbie");
            }
            T bean;
            if (beanInfo instanceof BeanFactory<T> beanFactory) {
                if (proxy) {
                    bean = factoryProxiedBean(type, BeanProxyType.JDK, beanFactory);
                } else if (beanInfo instanceof NamedBeanFactory<T> namedBeanFactory) {
                    bean = namedBeanFactory.factoryNamedBean(beanName, this);
                } else {
                    bean = beanFactory.factoryBean(this);
                }
            } else {
                bean = beanInfo.supply(this).get();
            }
            if (throwException && bean == null) {
                throw new BeanCreatedException("create bean " + type + " with name [" + beanName + "]) error");
            }
            return bean;
        }
    }

    /**
     * if isCreated() and isProxiedBean()
     * return getCreatedBean();
     * else
     * factory and proxy
     *
     * @param beanInterface bean's interface
     * @param proxyType bean proxy type
     * @param beanFactory   bean's factory
     * @return BEAN's proxy
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <T> T factoryProxiedBean(Class beanInterface, BeanProxyType proxyType, BeanFactory<T> beanFactory) {
        T bean;
        if (!beanFactory.isCreated()) {
            bean = beanFactory.factoryBean(this);
        } else {
            bean = beanFactory.getCreatedBean();
        }
        if (beanFactory.isCreated() && beanInterface != null && beanInterface.isInterface() && beanInterface.isInstance(bean)) {
            bean = beanFactory.getCreatedBean();
            if (!(bean instanceof Proxy)) {
                Set<BeanLifecycle> beanLifecycles = this.getBeanLifecycle();
                for (BeanLifecycle beanLifecycle : beanLifecycles) {
                    if (beanLifecycle.support(beanFactory.getBeanClass()) && beanLifecycle.support(beanFactory)) {
                        T proxy = (T) beanLifecycle.doPreCreated(beanFactory, bean, beanInterface, proxyType);
                        if (proxy instanceof Proxy) {
                            return proxy;
                        }
                    }
                }
            }
            return bean;
        }
        return bean;
    }

    @Override
    public <T> T factory(String beanName, Class<T> type) {
        return factory(beanName, type, true, true, true);
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
                    Bean bean = beanInfo.supply(this).get();
                    if (bean == null) {
                        throw new BeanCreatedException("factory bean (" + superType + ") error");
                    }
                    result.add(bean);
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
