package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.server.BaseServerProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/12 23:58.
 */
public class UndertowProperties extends BaseServerProperties<UndertowConfiguration> {
    private UndertowConfiguration configuration;

    @Override
    public UndertowConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        if (configuration != null) {
            return configuration;
        }

        ClassLoader classLoader = beanFactoryHandler.getClassLoader();
        configuration = new UndertowConfiguration(classLoader);

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);

        MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        UndertowProperties properties = new UndertowProperties();
        properties.loadAndSet(properties, configuration);

        return configuration;
    }
}
