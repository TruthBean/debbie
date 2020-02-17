package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieProperties;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RmiServerProperties extends BaseProperties implements DebbieProperties {

    private final RmiServerConfiguration configuration;

    //=================================================================================================================
    private static final String RMI_SERVER_HOST = "debbie.rmi.server.host";
    private static final String RMI_SERVER_PORT = "debbie.rmi.server.port";

    //=================================================================================================================

    public RmiServerProperties() {
        configuration = new RmiServerConfiguration();
        configuration.setRmiBindAddress(getStringValue(RMI_SERVER_HOST, "localhost"));
        configuration.setRmiBindPort(getIntegerValue(RMI_SERVER_PORT, 8040));
    }

    @Override
    public RmiServerConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        return configuration;
    }
}
