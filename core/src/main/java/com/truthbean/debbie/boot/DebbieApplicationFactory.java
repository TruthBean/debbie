package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplicationFactory extends BeanFactoryHandler {
    private final AbstractApplicationFactory application = loadApplication();

    private DebbieConfigurationFactory configurationFactory;
    public DebbieApplicationFactory(){
        super();
        // properties
        configurationFactory = new DebbieConfigurationFactory();
    }

    private AbstractApplicationFactory loadApplication() {
        var classLoader = ClassLoaderUtils.getClassLoader(DebbieApplicationFactory.class);
        AbstractApplicationFactory factory = null;
        try {
            factory = SpiLoader.loadProvider(AbstractApplicationFactory.class, classLoader);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return factory;
    }

    public void config() {
        LOGGER.debug("init configuration");
        // beanInitialization
        var beanInitialization = super.getBeanInitialization();
        BeanScanConfiguration configuration = ClassesScanProperties.toConfiguration();
        var targetClasses = configuration.getTargetClasses();
        beanInitialization.init(targetClasses);
        super.refreshBeans();
        // beanConfiguration
        BeanConfigurationRegister register = new BeanConfigurationRegister();
        register.register(targetClasses);
        super.refreshBeans();
        // event
        EventListenerBeanRegister eventListenerBeanRegister = new EventListenerBeanRegister(this);
        eventListenerBeanRegister.register();
    }

    public void callStarter() {
        Set<DebbieModuleStarter> debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        if (!debbieModuleStarters.isEmpty()) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter : " + debbieModuleStarter);
                debbieModuleStarter.starter(configurationFactory, this);
            }
        }
    }

    public DebbieApplication factoryApplication() {
        LOGGER.debug("create debbieApplication ...");
        return application.factory(configurationFactory, this);
    }

    public DebbieConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    public BeanFactoryHandler getBeanFactoryHandler() {
        return this;
    }

    protected static DebbieApplication debbieApplication;
    protected static DebbieApplicationFactory debbieApplicationFactory;

    public static DebbieApplication factory() {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info("debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null) return debbieApplication;
        debbieApplicationFactory = new DebbieApplicationFactory();
        debbieApplicationFactory.config();
        debbieApplicationFactory.callStarter();
        debbieApplication = debbieApplicationFactory.factoryApplication();
        debbieApplication.setBeforeStartTime(beforeStartTime);
        return debbieApplication;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}
