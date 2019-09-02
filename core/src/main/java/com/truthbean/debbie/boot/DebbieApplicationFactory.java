package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanConfigurationRegister;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplicationFactory extends BeanFactoryHandler {
    private final AbstractApplicationFactory application = loadApplication();

    public DebbieApplicationFactory() {
        super();
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

        Set<DebbieModuleStarter> debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        if (!debbieModuleStarters.isEmpty()) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter (" + debbieModuleStarter + ") registerBean");
                debbieModuleStarter.registerBean(this);
            }
        }

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
        DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
        if (!debbieModuleStarters.isEmpty()) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter : " + debbieModuleStarter);
                debbieModuleStarter.starter(configurationFactory, this);
            }
        }
    }

    public DebbieApplication factoryApplication() {
        LOGGER.debug("create debbieApplication ...");
        DebbieConfigurationFactory configurationFactory = getConfigurationFactory();
        return application.factory(configurationFactory, this);
    }

    public BeanFactoryHandler getBeanFactoryHandler() {
        return this;
    }

    protected static DebbieApplication debbieApplication;
    protected static DebbieApplicationFactory debbieApplicationFactory;

    public static DebbieApplication factory() {
        long beforeStartTime = System.currentTimeMillis();
        LOGGER.info("debbie start time: " + (new Timestamp(beforeStartTime)));
        if (debbieApplication != null)
            return debbieApplication;
        debbieApplicationFactory = new DebbieApplicationFactory();
        debbieApplicationFactory.config();
        debbieApplicationFactory.callStarter();
        debbieApplication = debbieApplicationFactory.factoryApplication();
        debbieApplication.setBeforeStartTime(beforeStartTime);
        return debbieApplication;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}
