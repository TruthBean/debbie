package com.truthbean.debbie.boot;

import com.truthbean.debbie.boot.exception.NoApplicationProviderException;
import com.truthbean.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.properties.AbstractProperties;
import com.truthbean.debbie.core.reflection.ClassLoaderUtils;
import com.truthbean.debbie.core.spi.NoServiceProviderException;
import com.truthbean.debbie.core.spi.SpiLoader;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplicationFactory {
    private static final AbstractApplicationFactory application = loadApplication();

    private static AbstractApplicationFactory loadApplication() {
        /* not work in java 11*/
        var classLoader = ClassLoaderUtils.getClassLoader(DebbieApplicationFactory.class);
        return SpiLoader.loadProvider(AbstractApplicationFactory.class, classLoader);

        /*AbstractApplicationFactory search;
        ServiceLoader<AbstractApplicationFactory> serviceLoader = ServiceLoader.load(AbstractApplicationFactory.class);
        Iterator<AbstractApplicationFactory> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            search = iterator.next();
        } else {
            throw new NoApplicationProviderException();
        }
        return search;*/
    }

    public static <P extends AbstractProperties, C extends BeanScanConfiguration> DebbieApplication factory
            (Class<P> propertiesClass, Class<C> configurationClass) {
        // load from properties
        // C configuration = P.loadProperties();
        // return application.factory(configuration);
        return null;
    }

    public static <C extends BeanScanConfiguration> DebbieApplication factory(C configuration) {
        // by javaConfig
        return application.factory(configuration);
    }

    public static DebbieApplication factory() {
        var configuration = DebbieConfigurationFactory.factoryServer();
        return application.factory(configuration);
    }
}
