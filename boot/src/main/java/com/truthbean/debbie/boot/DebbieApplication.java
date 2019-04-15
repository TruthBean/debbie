package com.truthbean.debbie.boot;

import com.truthbean.debbie.boot.exception.NoApplicationProviderException;
import com.truthbean.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.properties.AbstractProperties;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:09.
 */
public class DebbieApplication {
    private static final AbstractApplicationFactory APPLICATION;

    static {
        APPLICATION = loadApplication();
    }

    private static AbstractApplicationFactory loadApplication() {
        AbstractApplicationFactory search;
        ServiceLoader<AbstractApplicationFactory> serviceLoader = ServiceLoader.load(AbstractApplicationFactory.class);
        Iterator<AbstractApplicationFactory> searchs = serviceLoader.iterator();
        if (searchs.hasNext()) {
            search = searchs.next();
        } else {
            throw new NoApplicationProviderException();
        }
        return search;
    }

    public <P extends AbstractProperties, C extends BeanScanConfiguration> DebbieApplication
            (Class<P> propertiesClass, Class<C> configurationClass) {
        // load from properties
        C configuration = P.loadProperties();
        APPLICATION.factory(configuration);
    }

    public <C extends BeanScanConfiguration> DebbieApplication(C configuration) {
        // by javaConfig
        APPLICATION.factory(configuration);
    }

    public void start(String... args) {
        APPLICATION.run(args);
    }

    public void stop(String... args) {
        APPLICATION.exit(args);
    }
}
