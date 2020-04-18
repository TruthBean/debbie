package com.truthbean.debbie.netty;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class NettyModuleStarter implements DebbieModuleStarter {

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(NettyProperties.class, NettyConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 33;
    }
}
