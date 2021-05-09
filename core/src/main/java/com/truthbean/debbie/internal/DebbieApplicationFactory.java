/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.debbie.DebbieVersion;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionConfigurer;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplicationFactory implements ApplicationFactory {

    private static final Instant beforeStartTime = Instant.now();

    private volatile Set<DebbieModuleStarter> debbieModuleStarters;
    private final DebbieBootApplicationResolver bootApplicationResolver;
    private final DebbieApplicationContext applicationContext;

    private final ReflectionConfigurer reflectionConfigurer;

    protected DebbieApplicationFactory(Class<?> applicationClass, String... args) {
        var applicationArgs = new ApplicationArgs(args);
        var envContent = new EnvironmentContentHolder();
        this.reflectionConfigurer = new ReflectionConfigurer(envContent);
        this.applicationContext = new DebbieApplicationContext(applicationClass, ClassLoaderUtils.getClassLoader(applicationClass), applicationArgs, envContent);
        applicationContext.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(applicationContext);
    }

    protected DebbieApplicationFactory(ClassLoader classLoader, String... args) {
        var applicationArgs = new ApplicationArgs(args);
        var envContent = new EnvironmentContentHolder();
        this.reflectionConfigurer = new ReflectionConfigurer(envContent);
        this.applicationContext = new DebbieApplicationContext(null, classLoader, applicationArgs, envContent);
        applicationContext.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(applicationContext);
    }

    protected DebbieApplicationFactory(Class<?> applicationClass, ClassLoader classLoader, String... args) {
        var applicationArgs = new ApplicationArgs(args);
        var envContent = new EnvironmentContentHolder();
        this.reflectionConfigurer = new ReflectionConfigurer(envContent);
        this.applicationContext = new DebbieApplicationContext(applicationClass, classLoader, applicationArgs, envContent);
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
            LOGGER.info("ApplicationFactory( " + result.getClass() + " ) loaded. ");
            return result;
        } catch (Exception e) {
            LOGGER.error("", e);
            return new SimpleApplicationFactory();
        }
    }

    @Override
    public ApplicationFactory init(Class<?> applicationClass) {
        config(applicationClass);
        return this;
    }

    @Override
    public ApplicationFactory init() {
        config();
        return this;
    }

    protected synchronized void config(Class<?> applicationClass) {
        LOGGER.debug(() -> "init configuration");
        var classLoader = applicationContext.getClassLoader();
        var configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(applicationClass, configuration, applicationContext.getResourceResolver());
        applicationContext.getInjectedBeanFactory().registerInjectType(configuration.getCustomInjectType());
        config(configuration);
    }

    protected synchronized void config() {
        LOGGER.debug(() -> "init configuration");
        var classLoader = applicationContext.getClassLoader();
        var configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(null, configuration, applicationContext.getResourceResolver());
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
    public ApplicationFactory registerModuleStarter(DebbieModuleStarter moduleStarter) {
        DebbieModuleStarterRegister.getInstance().registerModuleStarter(moduleStarter);
        return this;
    }

    @Override
    public void config(BeanScanConfiguration configuration) {
        ResourceResolver resourceResolver = applicationContext.getResourceResolver();
        final var targetClasses = configuration.getTargetClasses(resourceResolver);
        // beanInitialization
        var beanInitialization = applicationContext.getBeanInitialization();
        // register annotation
        if (this.debbieModuleStarters == null) {
            Set<DebbieModuleStarter> debbieModuleStarterSet = DebbieModuleStarterRegister.getInstance().getDebbieModuleStarters();
            if (!debbieModuleStarterSet.isEmpty()) {
                this.debbieModuleStarters = new TreeSet<>(debbieModuleStarterSet);
            }
        }

        if (!this.debbieModuleStarters.isEmpty()) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(applicationContext.getEnvContent())) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") getComponentAnnotation");
                    Map<Class<? extends Annotation>, BeanComponentParser> componentAnnotations = debbieModuleStarter.getComponentAnnotation();
                    if (componentAnnotations != null && !componentAnnotations.isEmpty())
                        componentAnnotations.forEach(beanInitialization::registerBeanAnnotation);
                }
            }
        }

        if (reflectionConfigurer.isReflectEnable()) {
            beanInitialization.init(targetClasses);
        }
        registerResourceResolver(resourceResolver, beanInitialization);
        applicationContext.getBeanInfoFactory().refreshBeans();

        if (!debbieModuleStarters.isEmpty()) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(applicationContext.getEnvContent())) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") registerBean");
                    debbieModuleStarter.registerBean(applicationContext, beanInitialization);
                }
            }

            DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(applicationContext.getEnvContent())) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") configure");
                    debbieModuleStarter.configure(configurationFactory, applicationContext);
                }
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
            debbieModuleStarters = DebbieModuleStarterRegister.getInstance().getDebbieModuleStarters();
        }
        DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(applicationContext.getEnvContent())) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") start");
                    debbieModuleStarter.starter(configurationFactory, applicationContext);
                }
            }
        }
    }

    public void postCallStarter() {
        applicationContext.postCallStarter();
        Set<DebbieModuleStarter> debbieModuleStarterSet = this.debbieModuleStarters;
        if (debbieModuleStarterSet == null) {
            debbieModuleStarterSet = DebbieModuleStarterRegister.getInstance().getDebbieModuleStarters();
        }
        if (!debbieModuleStarterSet.isEmpty()) {
            debbieModuleStarterSet = new TreeSet<>(debbieModuleStarterSet);
        }
        if (debbieModuleStarters == null) {
            debbieModuleStarters = debbieModuleStarterSet;
        }
        for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
            if (debbieModuleStarter.enable(applicationContext.getEnvContent())) {
                LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") postStarter");
                debbieModuleStarter.postStarter(applicationContext);
            }
        }
    }

    @Override
    public void release() {
        synchronized (DebbieApplicationFactory.class) {
            LOGGER.debug("release all");
            if (debbieModuleStarters == null) {
                debbieModuleStarters = DebbieModuleStarterRegister.getInstance().getDebbieModuleStarters();
            }
            applicationContext.beforeRelease();
            if (!debbieModuleStarters.isEmpty()) {
                List<DebbieModuleStarter> list = new ArrayList<>(debbieModuleStarters);
                list.sort(Comparator.reverseOrder());
                DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
                for (DebbieModuleStarter debbieModuleStarter : list) {
                    if (debbieModuleStarter.enable(applicationContext.getEnvContent())) {
                        LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") release");
                        debbieModuleStarter.release(configurationFactory, applicationContext);
                    }
                }
                list.clear();
            }
            applicationContext.releaseBeans();

            // reset debbieApplication and debbieApplicationFactory
            debbieApplication = null;
            debbieApplicationFactory = null;
            // call gc
            System.gc();
        }
    }

    @Override
    public DebbieApplication factoryApplication() {
        LOGGER.debug("create debbieApplication ...");
        DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
        DebbieApplication application = loadApplication()
                .init(configurationFactory, applicationContext, applicationContext.getClassLoader());
        if (application == null) {
            LOGGER.info("ApplicationFactory fall fast to " + SimpleApplicationFactory.class + ". ");
            SimpleApplicationFactory simpleApplicationFactory = new SimpleApplicationFactory();
            simpleApplicationFactory.setApplicationFactory(this);
            return simpleApplicationFactory
                    .init(configurationFactory, applicationContext, applicationContext.getClassLoader());
        }
        return application;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected static volatile DebbieApplication debbieApplication;
    protected static volatile DebbieApplicationFactory debbieApplicationFactory;

    public static ApplicationFactory custom(Class<?> applicationClass, String... args) {
        printApplicationInfo();
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(applicationClass);
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        return new DebbieApplicationFactory(applicationClass, classLoader, args);
    }

    public static ApplicationFactory custom(String... args) {
        printApplicationInfo();
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        return new DebbieApplicationFactory(classLoader, args);
    }

    public static DebbieApplication create(Class<?> applicationClass, String... args) {
        printApplicationInfo();
        if (debbieApplication != null)
            return debbieApplication;
        var debbieApplicationFactory = configure(applicationClass, args);
        debbieApplication = debbieApplicationFactory.factoryApplication();
        debbieApplicationFactory.configDebbieApplication(debbieApplication);
        return debbieApplication;
    }

    public static DebbieApplication create(String... args) {
        printApplicationInfo();
        if (debbieApplication != null)
            return debbieApplication;
        var debbieApplicationFactory = configure(args);
        debbieApplication = debbieApplicationFactory.factoryApplication();
        debbieApplicationFactory.configDebbieApplication(debbieApplication);
        return debbieApplication;
    }

    private static void printApplicationInfo() {
        var pid = ManagementFactory.getRuntimeMXBean().getPid();
        LOGGER.info(() -> "debbie (" + DebbieVersion.getVersion() + ") application with PID " + pid + " start at " + beforeStartTime.toString());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("os.name: " + System.getProperty("os.name"));
            LOGGER.debug("os.arch: " + System.getProperty("os.arch"));

            LOGGER.debug("user.home: " + System.getProperty("user.home"));
            LOGGER.debug("user.dir: " + System.getProperty("user.dir"));
            LOGGER.debug("java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));

            LOGGER.debug("java.runtime.version: " + System.getProperty("java.runtime.version"));
            LOGGER.debug("java.home: " + System.getProperty("java.home"));
            LOGGER.debug("java.vm.vendor: " + System.getProperty("java.vm.vendor"));
            LOGGER.debug("java.library.path: " + System.getProperty("java.library.path"));
        }
    }

    private void configDebbieApplication(DebbieApplication debbieApplication) {
        if (debbieApplication instanceof AbstractApplication) {
            var application = (AbstractApplication) debbieApplication;
            application.setBeforeStartTime(beforeStartTime);
        }
    }

    public static synchronized DebbieApplicationFactory configure(Class<?> applicationClass, String... args) {
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(applicationClass);
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        debbieApplicationFactory = new DebbieApplicationFactory(applicationClass, classLoader, args);
        debbieApplicationFactory.config(applicationClass);
        debbieApplicationFactory.callStarter();
        return debbieApplicationFactory;
    }

    public static synchronized DebbieApplicationFactory configure(String... args) {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        debbieApplicationFactory = new DebbieApplicationFactory(classLoader, args);
        debbieApplicationFactory.config();
        debbieApplicationFactory.callStarter();
        return debbieApplicationFactory;
    }

    public static DebbieApplicationFactory configure(ClassLoader classLoader, Consumer<BeanScanConfiguration> consumer, String... args) {
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        debbieApplicationFactory = new DebbieApplicationFactory(classLoader, args);
        debbieApplicationFactory.config(consumer);
        debbieApplicationFactory.callStarter();
        return debbieApplicationFactory;
    }

    @Override
    public DebbieApplication postCreateApplication() {
        printApplicationInfo();
        if (debbieApplication != null)
            return debbieApplication;
        debbieApplication = factoryApplication();
        this.configDebbieApplication(debbieApplication);
        return debbieApplication;
    }

    @Override
    public DebbieApplication createApplication(Class<?> applicationClass) {
        printApplicationInfo();
        if (debbieApplication != null)
            return debbieApplication;

        debbieApplicationFactory = this;

        config(applicationClass);
        callStarter();

        debbieApplication = factoryApplication();
        debbieApplicationFactory.configDebbieApplication(debbieApplication);
        return debbieApplication;
    }

    @Override
    public ApplicationFactory createApplicationFactory() {
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;

        callStarter();
        debbieApplicationFactory = this;

        return this;
    }

    @Override
    public DebbieApplication createApplication() {
        if (debbieApplication != null)
            return debbieApplication;
        debbieApplication = factoryApplication();
        this.configDebbieApplication(debbieApplication);
        return debbieApplication;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}