package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanConfigurationRegister;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    public void callStarter() {
        Set<DebbieModuleStarter> debbieModuleStarters = SpiLoader.loadProviders(DebbieModuleStarter.class);
        if (debbieModuleStarters != null) {
            for (DebbieModuleStarter debbieModuleStarter : debbieModuleStarters) {
                LOGGER.debug("debbieModuleStarter : " + debbieModuleStarter);
                debbieModuleStarter.starter(configurationFactory, this);
            }
        }
    }

    public DebbieApplication factoryApplication() {
        return application.factory(configurationFactory, this);
    }

    public DebbieConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    public BeanFactoryHandler getBeanFactoryHandler() {
        return this;
    }

    public static DebbieApplication factory() {
        DebbieApplicationFactory factory = new DebbieApplicationFactory();
        factory.config();
        factory.callStarter();
        return factory.factoryApplication();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationFactory.class);
}
