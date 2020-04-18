package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.event.AbstractDebbieStartedEventListener;
import com.truthbean.debbie.event.DebbieStartedEvent;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.debbie.task.TaskFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplicationFactory extends BeanFactoryHandler {
    // private final AbstractApplicationFactory application = loadApplication();

    private Set<DebbieModuleStarter> debbieModuleStarters;
    private final DebbieBootApplicationResolver bootApplicationResolver;

    protected DebbieApplicationFactory(Class<?> applicationClass) {
        super(ClassLoaderUtils.getClassLoader(applicationClass));
        bootApplicationResolver = new DebbieBootApplicationResolver(this);
    }

    protected DebbieApplicationFactory(ClassLoader classLoader) {
        super(classLoader);
        bootApplicationResolver = new DebbieBootApplicationResolver(this);
    }

    private AbstractApplicationFactory loadApplication() {
        var classLoader = ClassLoaderUtils.getClassLoader(DebbieApplicationFactory.class);
        AbstractApplicationFactory result = null;
        AbstractApplicationFactory mockApplicationFactory = null;
        try {
            Collection<AbstractApplicationFactory> factories = SpiLoader.loadProviders(AbstractApplicationFactory.class, classLoader);
            for (AbstractApplicationFactory factory : factories) {
                var factoryClass = factory.getClass().getName();
                if ("com.truthbean.debbie.test.MockApplicationFactory".equals(factoryClass)) {
                    mockApplicationFactory = factory;
                    continue;
                } else {
                    result = factory;
                    break;
                }
            }
            if (result == null && mockApplicationFactory != null) {
                result = mockApplicationFactory;
            }
            LOGGER.debug("ApplicationFactory( " + result.getClass() + " ) loaded. ");
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return result;
    }

    protected void config(Class<?> applicationClass) {
        LOGGER.debug("init configuration");
        // beanInitialization
        var beanInitialization = super.getBeanInitialization();

        var classLoader = getClassLoader();
        BeanScanConfiguration configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(applicationClass, configuration, getResourceResolver());
        var targetClasses = configuration.getTargetClasses(getResourceResolver());
        beanInitialization.init(targetClasses);
        super.refreshBeans();

        debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter.toStr() + ") registerBean");
                debbieModuleStarter.registerBean(this, beanInitialization);
            }

            DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter.toStr() + ") configure");
                debbieModuleStarter.configure(configurationFactory, this);
            }
        }

        // beanConfiguration
        beanInitialization.registerBeanConfiguration(targetClasses);
        super.refreshBeans();
        // transformer
        DataTransformerFactory.register(targetClasses);
        // event
        EventListenerBeanRegister eventListenerBeanRegister = new EventListenerBeanRegister(this);
        eventListenerBeanRegister.register();
    }

    protected void callStarter() {
        if (debbieModuleStarters == null) {
            debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        }
        DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter.toStr() + ") start");
                debbieModuleStarter.starter(configurationFactory, this);
            }
        }

        // do startedEvent
        multicastEvent(this);
        // do task
        TaskFactory taskFactory = super.factory("taskFactory");
        taskFactory.doTask();
    }

    private void multicastEvent(BeanFactoryHandler beanFactoryHandler) {
        DebbieStartedEvent startedEvent = new DebbieStartedEvent(this, beanFactoryHandler);
        List<AbstractDebbieStartedEventListener> beanInfoList = beanFactoryHandler.getBeanList(AbstractDebbieStartedEventListener.class);
        if (beanInfoList != null) {
            for (AbstractDebbieStartedEventListener startedEventListener : beanInfoList) {
                startedEventListener.onEvent(startedEvent);
            }
        }
    }

    @Override
    public void release(String... args) {
        LOGGER.debug("release all");
        if (debbieModuleStarters == null) {
            debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        }
        if (!debbieModuleStarters.isEmpty()) {
            List<DebbieModuleStarter> list = new ArrayList<>(debbieModuleStarters);
            Collections.sort(list, (starter1, starter2) -> {
                return starter2.compareTo(starter1);
            });
            DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
            for (DebbieModuleStarter debbieModuleStarter : list) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter.toStr() + ") release");
                debbieModuleStarter.release(configurationFactory, this);
            }
        }
        super.releaseBeans();
    }

    public DebbieApplication factoryApplication() {
        LOGGER.debug("create debbieApplication ...");
        DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
        return loadApplication().factory(configurationFactory, this, getClassLoader());
    }

    public BeanFactoryHandler getBeanFactoryHandler() {
        return this;
    }

    protected volatile static DebbieApplication debbieApplication;
    protected volatile static DebbieApplicationFactory debbieApplicationFactory;

    public static DebbieApplication create(Class<?> applicationClass) {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info("debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null)
            return debbieApplication;
        var debbieApplicationFactory = configure(applicationClass);
        debbieApplication = debbieApplicationFactory.factoryApplication();
        debbieApplication.setBeforeStartTime(beforeStartTime);
        return debbieApplication;
    }

    public static DebbieApplicationFactory configure(Class<?> applicationClass) {
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(applicationClass);
        if (debbieApplicationFactory != null)
            return debbieApplicationFactory;
        debbieApplicationFactory = new DebbieApplicationFactory(classLoader);
        debbieApplicationFactory.config(applicationClass);
        debbieApplicationFactory.callStarter();
        return debbieApplicationFactory;
    }

    /**
     * @deprecated removed after next version
     * @return new DebbieApplication
     */
    public static DebbieApplication factory() {
        return create(DebbieApplicationFactory.class);
    }

    public DebbieApplication createApplication() {
        return this.createApplication(DebbieApplicationFactory.class);
    }

    public DebbieApplication postCreateApplication() {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info("debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null)
            return debbieApplication;
        debbieApplication = factoryApplication();
        debbieApplication.setBeforeStartTime(beforeStartTime);
        return debbieApplication;
    }

    public DebbieApplication createApplication(Class<?> applicationClass) {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info("debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null)
            return debbieApplication;

        debbieApplicationFactory = this;

        config(applicationClass);
        callStarter();
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(applicationClass);
        debbieApplication = factoryApplication();
        debbieApplication.setBeforeStartTime(beforeStartTime);
        return debbieApplication;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}
