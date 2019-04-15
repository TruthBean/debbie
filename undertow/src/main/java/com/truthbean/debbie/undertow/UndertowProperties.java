package com.truthbean.debbie.undertow;

import com.truthbean.debbie.boot.AbstractServerProperties;
import com.truthbean.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.properties.ClassesScanProperties;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/12 23:58.
 */
public class UndertowProperties extends AbstractServerProperties {
    public static UndertowConfiguration loadProperties() {
        UndertowConfiguration configuration = new UndertowConfiguration();

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.getBeanConfiguration();
        MvcConfiguration mvcConfiguration = MvcProperties.loadProperties();
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        UndertowProperties properties = new UndertowProperties();
        properties.loadAndSet(properties, configuration);

        return configuration;
    }
}
