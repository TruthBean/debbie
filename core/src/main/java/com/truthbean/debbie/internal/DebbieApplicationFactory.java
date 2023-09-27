/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.event.EventListenerBeanManager;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.proxy.BeanProxyType;
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

    private final ReflectionConfigurer reflectionConfigurer;

    private final EnvironmentDepositoryHolder environmentDepositoryHolder;
    private final Environment environment;

    protected DebbieApplicationFactory() {
        environmentDepositoryHolder = new DebbieEnvironmentDepositoryHolder();
        environment = environmentDepositoryHolder.getEnvironmentIfPresent(environmentDepositoryHolder.getDefaultProfile());
        this.reflectionConfigurer = new ReflectionConfigurer(environment);
    }

    public static DebbieApplicationFactory newEmpty() {
        DebbieApplicationFactory.printApplicationInfo();
        return new DebbieApplicationFactory();
    }

    private ApplicationArgs applicationArgs;
    private Class<?> applicationClass;

    private volatile Set<DebbieModuleStarter> debbieModuleStarters;
    private DebbieBootApplicationResolver bootApplicationResolver;
    private DebbieApplicationContext applicationContext;

    private synchronized AbstractApplication loadApplication() {
        var classLoader = applicationContext.getClassLoader();
        try {
            AbstractApplication result = null;
            Set<AbstractApplication> set = SpiLoader.loadProviderSet(AbstractApplication.class, classLoader);
            if (set != null && !set.isEmpty()) {
                for (AbstractApplication application : set) {
                    if (application.isEnable(environment)) {
                        result = application;
                        break;
                    }
                }
            }
            if (result == null) {
                result = new SimpleApplication();
            }
            result.setApplicationFactory(this);
            LOGGER.info("ApplicationFactory( " + result.getClass() + " ) loaded. ");
            return result;
        } catch (Exception e) {
            LOGGER.error("", e);
            return new SimpleApplication();
        }
    }

    @Override
    public ApplicationFactory preInit(String... args) {
        applicationArgs = new ApplicationArgs(args);
        return this;
    }

    @Override
    public ApplicationFactory preInit(Class<?> applicationClass, String... args) {
        this.applicationClass = applicationClass;
        this.applicationArgs = new ApplicationArgs(args);
        return this;
    }

    @Override
    public ApplicationFactory registerModuleStarter(DebbieModuleStarter moduleStarter) {
        DebbieModuleStarterRegister.getInstance().registerModuleStarter(moduleStarter);
        return this;
    }

    @Override
    public ApplicationFactory init(Class<?>... beanClasses) {
        this.applicationContext = new DebbieApplicationContext(applicationClass, ClassLoaderUtils.getClassLoader(applicationClass), applicationArgs, environmentDepositoryHolder, beanClasses);
        applicationContext.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(applicationContext);
        return this;
    }

    @Override
    public ApplicationFactory init(ClassLoader classLoader, Class<?>... beanClasses) {
        this.applicationContext = new DebbieApplicationContext(applicationClass, classLoader, applicationArgs, environmentDepositoryHolder, beanClasses);
        applicationContext.postConstructor();
        bootApplicationResolver = new DebbieBootApplicationResolver(applicationContext);
        return this;
    }

    @Override
    public ApplicationFactory register(BeanInfo<?> beanInfo) {
        var beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanInfo(beanInfo);
        return this;
    }

    @Override
    public ApplicationFactory register(BeanFactory<?> beanFactory) {
        var beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanInfo(beanFactory);
        return this;
    }

    @Override
    public ApplicationFactory register(Collection<BeanInfo<?>> collection) {
        var beanInfoManager = applicationContext.getBeanInfoManager();
        for (BeanInfo<?> beanInfo : collection) {
            beanInfoManager.registerBeanInfo(beanInfo);
        }
        return this;
    }

    @Override
    public ApplicationFactory register(BeanLifecycle beanLifecycle) {
        var beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanLifecycle(beanLifecycle);
        return this;
    }

    @Override
    public ApplicationFactory register(BeanRegister beanRegister) {
        var beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanRegister(beanRegister);
        return this;
    }

    @Override
    public ApplicationFactory config() {
        LOGGER.debug(() -> "init configuration");
        var classLoader = applicationContext.getClassLoader();
        var configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(applicationClass, configuration, applicationContext.getResourceResolver());
        applicationContext.getBeanInfoManager().registerInjectType(configuration.getCustomInjectType());
        config(configuration);
        return this;
    }

    @Override
    public <T> ApplicationFactory config(T application) {
        LOGGER.debug(() -> "init configuration");
        var classLoader = applicationContext.getClassLoader();
        var configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(application, configuration, applicationContext.getResourceResolver());
        applicationContext.getBeanInfoManager().registerInjectType(configuration.getCustomInjectType());
        config(configuration);
        return this;
    }

    @Override
    public ApplicationFactory config(BeanScanConfiguration configuration) {
        ResourceResolver resourceResolver = applicationContext.getResourceResolver();
        final var targetClasses = configuration.getTargetClasses(resourceResolver);
        // beanInfoManager
        var beanInfoManager = applicationContext.getBeanInfoManager();
        // register annotation
        if (this.debbieModuleStarters == null) {
            Set<DebbieModuleStarter> debbieModuleStarterSet = DebbieModuleStarterRegister.getInstance().getDebbieModuleStarters();
            if (!debbieModuleStarterSet.isEmpty()) {
                this.debbieModuleStarters = new TreeSet<>(debbieModuleStarterSet);
            }
        }

        if (!this.debbieModuleStarters.isEmpty()) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(environment)) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") getComponentAnnotation");
                    Map<Class<? extends Annotation>, BeanComponentParser> componentAnnotations = debbieModuleStarter.getComponentAnnotation();
                    if (componentAnnotations != null && !componentAnnotations.isEmpty()) {
                        componentAnnotations.forEach(beanInfoManager::registerBeanAnnotation);
                    }
                }
            }
        }

        registerResourceResolver(resourceResolver, beanInfoManager);

        if (!debbieModuleStarters.isEmpty()) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(environment)) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") registerBean");
                    debbieModuleStarter.registerBean(applicationContext, beanInfoManager);
                }
            }

            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(environment)) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") configure");
                    debbieModuleStarter.configure(applicationContext);
                }
            }
        }

        if (reflectionConfigurer.isReflectEnable()) {
            LOGGER.debug(() -> "register scanned bean ...");
            beanInfoManager.register(targetClasses);
        }

        EventListenerBeanRegister eventListenerBeanRegister = new EventListenerBeanRegister(applicationContext);
        EventListenerBeanManager register = eventListenerBeanRegister.register();
        applicationContext.setEventListenerBeanManager(register);
        return this;
    }

    @Override
    public ApplicationFactory create() {
        callStarter();
        return this;
    }

    @Override
    public ApplicationFactory postCreate() {
        return this;
    }

    @Override
    public ApplicationFactory build() {
        return this;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected static volatile DebbieApplication debbieApplication;

    @Override
    public DebbieApplication factory() {
        if (debbieApplication != null) {
            return debbieApplication;
        }
        debbieApplication = this.factoryApplication();
        this.configDebbieApplication(debbieApplication);
        return debbieApplication;
    }

    @Override
    public void release() {
        synchronized (DebbieApplicationFactory.class) {
            LOGGER.debug("destruct all");
            if (debbieModuleStarters == null) {
                debbieModuleStarters = DebbieModuleStarterRegister.getInstance().getDebbieModuleStarters();
            }
            applicationContext.beforeRelease();
            if (!debbieModuleStarters.isEmpty()) {
                List<DebbieModuleStarter> list = new ArrayList<>(debbieModuleStarters);
                list.sort(Comparator.reverseOrder());
                for (DebbieModuleStarter debbieModuleStarter : list) {
                    if (debbieModuleStarter.enable(environment)) {
                        LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") destruct");
                        debbieModuleStarter.release(applicationContext);
                    }
                }
                list.clear();
            }
            applicationContext.releaseBeans();

            // reset debbieApplication and debbieApplicationFactory
            debbieApplication = null;
            // call gc
            System.gc();
        }
    }

    public synchronized ApplicationFactory config(Class<?> applicationClass) {
        LOGGER.debug(() -> "init configuration");
        var classLoader = applicationContext.getClassLoader();
        var configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(applicationClass, configuration, applicationContext.getResourceResolver());
        applicationContext.getBeanInfoManager().registerInjectType(configuration.getCustomInjectType());
        config(configuration);
        return this;
    }

    public synchronized ApplicationFactory configApplication(Object application) {
        LOGGER.debug(() -> "init configuration");
        var classLoader = applicationContext.getClassLoader();
        var configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(application, configuration, applicationContext.getResourceResolver());
        applicationContext.getBeanInfoManager().registerInjectType(configuration.getCustomInjectType());
        config(configuration);
        return this;
    }

    protected synchronized void config(Consumer<BeanScanConfiguration> configurationConsumer) {
        LOGGER.debug("init configuration");
        var classLoader = applicationContext.getClassLoader();
        BeanScanConfiguration configuration = ClassesScanProperties.toConfiguration(classLoader);
        configurationConsumer.accept(configuration);
        bootApplicationResolver.resolverClasses(configuration, applicationContext.getResourceResolver());
        config(configuration);
    }

    private void registerResourceResolver(ResourceResolver resourceResolver, BeanInfoManager beanInfoManager) {
        SimpleBeanFactory<ResourceResolver, ResourceResolver> beanInfo = new SimpleBeanFactory<>(resourceResolver, ResourceResolver.class, BeanProxyType.NO, "resourceResolver");
        beanInfoManager.registerBeanInfo(beanInfo);
    }

    protected synchronized void callStarter() {
        if (debbieModuleStarters == null) {
            debbieModuleStarters = DebbieModuleStarterRegister.getInstance().getDebbieModuleStarters();
        }
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                if (debbieModuleStarter.enable(environment)) {
                    LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") start");
                    debbieModuleStarter.starter(applicationContext);
                }
            }
        }
    }

    public void postCallStarter(DebbieApplication application) {
        applicationContext.postCallStarter(application);
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
            if (debbieModuleStarter.enable(environment)) {
                LOGGER.info(() -> "debbieModuleStarter (" + debbieModuleStarter.toStr() + ") postStarter");
                debbieModuleStarter.postStarter(applicationContext);
            }
        }
    }

    public DebbieApplication factoryApplication() {
        LOGGER.debug("create debbieApplication ...");
        DebbieApplication application = loadApplication()
                .init(applicationContext, applicationContext.getClassLoader());
        if (application == null) {
            LOGGER.info("DebbieApplication fall fast to " + SimpleApplication.class + ". ");
            SimpleApplication simpleApplication = new SimpleApplication();
            simpleApplication.setApplicationFactory(this);
            return simpleApplication.init(applicationContext, applicationContext.getClassLoader());
        }
        return application;
    }

    static void printApplicationInfo() {
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
        if (debbieApplication instanceof AbstractApplication application) {
            application.setBeforeStartTime(beforeStartTime);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}