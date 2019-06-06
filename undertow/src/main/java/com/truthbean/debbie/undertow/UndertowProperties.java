package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.BaseServerProperties;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/12 23:58.
 */
public class UndertowProperties extends BaseServerProperties {
    private UndertowConfiguration configuration;

    @Override
    public UndertowConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new UndertowConfiguration();

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration();
        MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration();
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        UndertowProperties properties = new UndertowProperties();
        properties.loadAndSet(properties, configuration);

        return configuration;
    }
}
