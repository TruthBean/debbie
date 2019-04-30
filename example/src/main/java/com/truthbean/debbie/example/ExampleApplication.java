package com.truthbean.debbie.example;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieConfigurationFactory;
import com.truthbean.debbie.core.net.NetWorkUtils;
import com.truthbean.debbie.core.properties.AbstractProperties;
import com.truthbean.debbie.core.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-31 18:02
 */
public class ExampleApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleApplication.class);

    public static void main(String[] args) {
        var configuration = DebbieConfigurationFactory.factoryServer();
        var application = DebbieApplicationFactory.factory(configuration);
        application.start(args);
        LOGGER.debug("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());
    }

    /*public static void main(String[] args) {
        Map<Class<AbstractProperties>, Class> classClassMap = SpiLoader.loadPropertiesClasses();
        System.out.println(classClassMap);
    }*/
}