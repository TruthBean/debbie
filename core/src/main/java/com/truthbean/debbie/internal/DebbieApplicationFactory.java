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
public class DebbieApplicationFactory extends DebbieApplicationContext implements ApplicationFactory {

    private Set<DebbieModuleStarter> debbieModuleStarters;
    private final DebbieBootApplicationResolver bootApplicationResolver;

    protected DebbieApplicationFactory(Class<?> applicationClass) {
        super(applicationClass, ClassLoaderUtils.getClassLoader(applicationClass));
        super.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(this);
    }

    protected DebbieApplicationFactory(ClassLoader classLoader) {
        super(null, classLoader);
        super.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(this);
    }

    protected DebbieApplicationFactory(Class<?> applicationClass, ClassLoader classLoader) {
        super(applicationClass, classLoader);
        super.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(this);
    }

    private synchronized AbstractApplication loadApplication() {
        var classLoader = getClassLoader();
        try {
            var result = SpiLoader.loadProvider(AbstractApplication.class, classLoader, new SimpleApplicationFactory());
            if (result == null) {
                result = new SimpleApplicationFactory();
            }
            result.setApplicationContext(this);
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
        var classLoader = getClassLoader();
        BeanScanConfiguration configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(applicationClass, configuration, getResourceResolver());
        super.getInjectedBeanFactory().registerInjectType(configuration.getCustomInjectType());
        config(configuration);
    }

    protected synchronized void config(Consumer<BeanScanConfiguration> configurationConsumer) {
        LOGGER.debug("init configuration");
        var classLoader = getClassLoader();
        BeanScanConfiguration configuration = ClassesScanProperties.toConfiguration(classLoader);
        configurationConsumer.accept(configuration);
        bootApplicationResolver.resolverClasses(configuration, getResourceResolver());
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
        ResourceResolver resourceResolver = getResourceResolver();
        var targetClasses = configuration.getTargetClasses(resourceResolver);
        // beanInitialization
        var beanInitialization = super.getBeanInitialization();
        beanInitialization.init(targetClasses);
        registerResourceResolver(resourceResolver, beanInitialization);
        super.getDebbieBeanInfoFactory().refreshBeans();

        debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") registerBean");
                debbieModuleStarter.registerBean(this, beanInitialization);
            }

            DebbieConfigurationCenter configurationFactory = getConfigurationCenter();
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") configure");
                debbieModuleStarter.configure(configurationFactory, this);
            }
        }

        // beanConfiguration
        beanInitialization.registerBeanConfiguration(targetClasses);
        super.getDebbieBeanInfoFactory().refreshBeans();
        // transformer
        DataTransformerFactory.register(targetClasses);
        // event
        EventListenerBeanRegister eventListenerBeanRegister = new EventListenerBeanRegister(this);
        eventListenerBeanRegister.register();
    }

    protected synchronized void callStarter() {
        if (debbieModuleStarters == null) {
            debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        }
        DebbieConfigurationCenter configurationFactory = getConfigurationCenter();
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") start");
                debbieModuleStarter.starter(configurationFactory, this);
            }
        }
    }

    private volatile AutoCreatedBeanFactory autoCreatedBeanFactory;

    public synchronized void postCallStarter() {
        // create not lazy beans
        if (this.autoCreatedBeanFactory == null) {
            autoCreatedBeanFactory = new AutoCreatedBeanFactory(this);
        }
        autoCreatedBeanFactory.autoCreateBeans();
        // do startedEvent
        multicastEvent(this);
        // do task
        TaskFactory taskFactory = super.getGlobalBeanFactory().factory("taskFactory");
        taskFactory.doTask();
    }

    private volatile DebbieStartedEventProcessor processor;

    private void multicastEvent(ApplicationContext applicationContext) {
        if (this.processor == null) {
            this.processor = new DebbieStartedEventProcessor(applicationContext);
        }
        processor.multicastEvent();
    }

    private synchronized void beforeRelease() {
        if (this.autoCreatedBeanFactory != null) {
            this.autoCreatedBeanFactory.stopAll();
        }
        if (this.processor != null) {
            processor.stopAll();
        }
    }

    @Override
    public void release(String... args) {
        LOGGER.debug("release all");
        if (debbieModuleStarters == null) {
            debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        }
        beforeRelease();
        if (!debbieModuleStarters.isEmpty()) {
            List<DebbieModuleStarter> list = new ArrayList<>(debbieModuleStarters);
            list.sort(Comparator.reverseOrder());
            DebbieConfigurationCenter configurationFactory = getConfigurationCenter();
            for (DebbieModuleStarter debbieModuleStarter : list) {
                LOGGER.debug(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") release");
                debbieModuleStarter.release(configurationFactory, this);
            }
        }
        super.releaseBeans();
    }

    @Override
    public DebbieApplication factoryApplication() {
        LOGGER.debug("create debbieApplication ...");
        DebbieConfigurationCenter configurationFactory = getConfigurationCenter();
        return loadApplication().init(configurationFactory, this, getClassLoader());
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this;
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

    public static DebbieApplicationFactory configure(Class<?> applicationClass) {
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