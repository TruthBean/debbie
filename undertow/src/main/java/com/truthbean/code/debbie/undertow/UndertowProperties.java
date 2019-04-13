package com.truthbean.code.debbie.undertow;

import com.truthbean.code.debbie.boot.AbstractServerProperties;
import com.truthbean.code.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.code.debbie.core.properties.ClassesScanProperties;
import com.truthbean.code.debbie.mvc.MvcConfiguration;
import com.truthbean.code.debbie.mvc.MvcProperties;

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
