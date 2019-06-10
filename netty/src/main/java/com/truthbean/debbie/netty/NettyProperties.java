package com.truthbean.debbie.netty;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.server.BaseServerProperties;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/30 22:37.
 */
public class NettyProperties extends BaseServerProperties {

    private NettyConfiguration configuration;

    @Override
    public NettyConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new NettyConfiguration();

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration();
        MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration();
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        NettyProperties properties = new NettyProperties();
        properties.loadAndSet(properties, configuration);

        return configuration;
    }
}
