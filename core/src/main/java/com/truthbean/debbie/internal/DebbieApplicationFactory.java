/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.event.DebbieStartedEventProcessor;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.debbie.task.TaskFactory;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplicationFactory implements ApplicationFactory {

    private Set<DebbieModuleStarter> debbieModuleStarters;
    private final DebbieBootApplicationResolver bootApplicationResolver;
    private final DebbieApplicationContext applicationContext;

    protected DebbieApplicationFactory(Class<?> applicationClass) {
        this.applicationContext = new DebbieApplicationContext(applicationClass, ClassLoaderUtils.getClassLoader(applicationClass));
        applicationContext.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(applicationContext);
    }

    protected DebbieApplicationFactory(ClassLoader classLoader) {
        this.applicationContext = new DebbieApplicationContext(null, classLoader);
        applicationContext.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(applicationContext);
    }

    protected DebbieApplicationFactory(Class<?> applicationClass, ClassLoader classLoader) {
        this.applicationContext = new DebbieApplicationContext(applicationClass, classLoader);
        applicationContext.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(applicationContext);
    }

    private synchronized AbstractApplication loadApplication() {
        var classLoader = applicationContext.getClassLoader();
        try {
            var result = SpiLoader.loadProvider(AbstractApplication.class, classLoader, new SimpleApplicationFactory());
            if (result == null) {
                result = new SimpleApplicationFactory();
            }
            result.setApplicationFactory(this);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("ApplicationFactory( " + result.getClass() + " ) loaded. ");
            return result;
        } catch (Exception e) {
            LOGGER.error("", e);
            return new SimpleApplicationFactory();
        }
    }

    protected synchronized void config(Class<?> applicationClass) {
        LOGGER.debug(() -> "init configuration");
        var classLoader = applicationContext.getClassLoader();
        var configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(applicationClass, configuration, applicationContext.getResourceResolver());
        applicationContext.getInjectedBeanFactory().registerInjectType(configuration.getCustomInjectType());
        config(configuration);
    }

    protected synchronized void config(Consumer<BeanScanConfiguration> configurationConsumer) {
        LOGGER.debug("init configuration");
        var classLoader = applicationContext.getClassLoader();
        BeanScanConfiguration configuration = ClassesScanProperties.toConfiguration(classLoader);
        configurationConsumer.accept(configuration);
        bootApplicationResolver.resolverClasses(configuration, applicationContext.getResourceResolver());
        config(configuration);
    }

    private void registerResourceResolver(ResourceResolver resourceResolver, BeanInitialization initialization) {
        DebbieBeanInfo<ResourceResolver> beanInfo = new DebbieBeanInfo<>(ResourceResolver.class);
        beanInfo.setBean(resourceResolver);
        beanInfo.addBeanName("resourceResolver");
        initialization.initSingletonBean(beanInfo);
    }

    @Override
    public void config(BeanScanConfiguration configuration) {
        ResourceResolver resourceResolver = applicationContext.getResourceResolver();
        final var targetClasses = configuration.getTargetClasses(resourceResolver);
        // beanInitialization
        var beanInitialization = applicationContext.getBeanInitialization();
        beanInitialization.init(targetClasses);
        registerResourceResolver(resourceResolver, beanInitialization);
        applicationContext.getBeanInfoFactory().refreshBeans();

        debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") registerBean");
                debbieModuleStarter.registerBean(applicationContext, beanInitialization);
            }

            DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") configure");
                debbieModuleStarter.configure(configurationFactory, applicationContext);
            }
        }

        // beanConfiguration
        beanInitialization.registerBeanConfiguration(targetClasses);
        applicationContext.getBeanInfoFactory().refreshBeans();
        // transformer
        DataTransformerFactory.register(targetClasses);
        // event
        EventListenerBeanRegister eventListenerBeanRegister = new EventListenerBeanRegister(applicationContext);
        eventListenerBeanRegister.register();
    }

    protected synchronized void callStarter() {
        if (debbieModuleStarters == null) {
            debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        }
        DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") start");
                debbieModuleStarter.starter(configurationFactory, applicationContext);
            }
        }
    }

    public void postCallStarter() {
        applicationContext.postCallStarter();
    }

    @Override
    public void release(String... args) {
        synchronized (DebbieApplicationFactory.class) {
            LOGGER.debug("release all");
            if (debbieModuleStarters == null) {
                debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
            }
            applicationContext.beforeRelease();
            if (!debbieModuleStarters.isEmpty()) {
                List<DebbieModuleStarter> list = new ArrayList<>(debbieModuleStarters);
                list.sort(Comparator.reverseOrder());
                DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
                for (DebbieModuleStarter debbieModuleStarter : list) {
                    LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") release");
                    debbieModuleStarter.release(configurationFactory, applicationContext);
                }
                list.clear();
            }
            applicationContext.releaseBeans();

            // reset debbieApplication and debbieApplicationFactory
            debbieApplication = null;
            debbieApplicationFactory = null;
        }
    }

    @Override
    public DebbieApplication factoryApplication() {
        LOGGER.debug("create debbieApplication ...");
        DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
        return loadApplication().init(configurationFactory, applicationContext, applicationContext.getClassLoader());
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected static volatile DebbieApplication debbieApplication;
    protected static volatile DebbieApplicationFactory debbieApplicationFactory;

    public static DebbieApplication create(Class<?> applicationClass) {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info(() -> "debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null)
            return debbieApplication;
        var debbieApplicationFactory = configure(applicationClass);
        debbieApplication = debbieApplicationFactory.factoryApplication();
        debbieApplicationFactory.configDebbieApplication(debbieApplication, beforeStartTime);
        return debbieApplication;
    }

    private void configDebbieApplication(DebbieApplication debbieApplication, long beforeStartTime) {
        if (debbieApplication instanceof AbstractApplication) {
            var application = (AbstractApplication) debbieApplication;
            application.setBeforeStartTime(beforeStartTime);
        }
    }

    public static synchronized DebbieApplicationFactory configure(Class<?> applicationClass) {
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(applicationClass);
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        debbieApplicationFactory = new DebbieApplicationFactory(applicationClass, classLoader);
        debbieApplicationFactory.config(applicationClass);
        debbieApplicationFactory.callStarter();
        return debbieApplicationFactory;
    }

    public static DebbieApplicationFactory configure(ClassLoader classLoader, Consumer<BeanScanConfiguration> consumer) {
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        debbieApplicationFactory = new DebbieApplicationFactory(classLoader);
        debbieApplicationFactory.config(consumer);
        debbieApplicationFactory.callStarter();
        return debbieApplicationFactory;
    }

    @Override
    public DebbieApplication postCreateApplication() {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info(() -> "debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null)
            return debbieApplication;
        debbieApplication = factoryApplication();
        this.configDebbieApplication(debbieApplication, beforeStartTime);
        return debbieApplication;
    }

    @Override
    public DebbieApplication createApplication(Class<?> applicationClass) {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info(() -> "debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null)
            return debbieApplication;

        debbieApplicationFactory = this;

        config(applicationClass);
        callStarter();

        debbieApplication = factoryApplication();
        debbieApplicationFactory.configDebbieApplication(debbieApplication, beforeStartTime);
        return debbieApplication;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}