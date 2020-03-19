package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanConfigurationRegister;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.DebbieConfigurationCenter;
import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.event.AbstractDebbieStartedEventListener;
import com.truthbean.debbie.event.DebbieStartedEvent;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplicationFactory extends BeanFactoryHandler {
    // private final AbstractApplicationFactory application = loadApplication();

    private Set<DebbieModuleStarter> debbieModuleStarters;
    private final DebbieBootApplicationResolver bootApplicationResolver;

    public DebbieApplicationFactory(Class<?> applicationClass) {
        super(ClassLoaderUtils.getClassLoader(applicationClass));
        bootApplicationResolver = new DebbieBootApplicationResolver(this);
    }

    public DebbieApplicationFactory(ClassLoader classLoader) {
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

    public void config() {
        this.config(null);
    }

    public void config(Class<?> applicationClass) {
        LOGGER.debug("init configuration");
        // beanInitialization
        var beanInitialization = super.getBeanInitialization();

        ClassLoader classLoader = getClassLoader();
        BeanScanConfiguration configuration = ClassesScanProperties.toConfiguration(classLoader);
        bootApplicationResolver.resolverApplicationClass(applicationClass, configuration);
        var targetClasses = configuration.getTargetClasses();
        beanInitialization.init(targetClasses);
        super.refreshBeans();

        debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter + ") registerBean");
                debbieModuleStarter.registerBean(this);
            }
        }

        // beanConfiguration
        BeanConfigurationRegister register = new BeanConfigurationRegister();
        register.register(targetClasses);
        super.refreshBeans();
        // transformer
        DataTransformerFactory.register(targetClasses);
        // event
        EventListenerBeanRegister eventListenerBeanRegister = new EventListenerBeanRegister(this);
        eventListenerBeanRegister.register();
    }

    public void callStarter() {
        if (debbieModuleStarters == null) {
            debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        }
        DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
        if (!debbieModuleStarters.isEmpty()) {
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter + ") start");
                debbieModuleStarter.starter(configurationFactory, this);
            }
        }

        // do startedEvent
        multicastEvent(this);
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
            debbieModuleStarters = new TreeSet<>(debbieModuleStarters);
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter + ") release");
                debbieModuleStarter.release();
            }
        }
        super.release(args);
    }

    public DebbieApplication factoryApplication(ClassLoader classLoader) {
        LOGGER.debug("create debbieApplication ...");
        DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
        return loadApplication().factory(configurationFactory, this, classLoader);
    }

    public BeanFactoryHandler getBeanFactoryHandler() {
        return this;
    }

    protected volatile static DebbieApplication debbieApplication;
    protected volatile static DebbieApplicationFactory debbieApplicationFactory;

    public static DebbieApplication create(Class<?> applicationClass) {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info("debbie start time: " + (new Timestamp(beforeStartTime)));
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(applicationClass);
        if (debbieApplication != null)
            return debbieApplication;
        debbieApplicationFactory = new DebbieApplicationFactory(classLoader);
        debbieApplicationFactory.config(applicationClass);
        debbieApplicationFactory.callStarter();
        debbieApplication = debbieApplicationFactory.factoryApplication(classLoader);
        debbieApplication.setBeforeStartTime(beforeStartTime);
        return debbieApplication;
    }

    /**
     * @deprecated removed after next version
     * @return new DebbieApplication
     */
    public static DebbieApplication factory() {
        return create(DebbieApplicationFactory.class);
    }

    public DebbieApplication createApplication() {
        return this.createApplication(null);
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
        debbieApplication = factoryApplication(classLoader);
        debbieApplication.setBeforeStartTime(beforeStartTime);
        return debbieApplication;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}
