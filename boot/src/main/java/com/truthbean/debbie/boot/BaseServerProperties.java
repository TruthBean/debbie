package com.truthbean.debbie.boot;

import com.truthbean.debbie.core.properties.BaseProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class BaseServerProperties extends BaseProperties {
    //===========================================================================
    private static final String SERVER_PORT = "debbie.server.port";
    private static final String SERVER_HOST = "debbie.server.host";

    //===========================================================================

    public <P extends BaseServerProperties, C extends AbstractServerConfiguration> void loadAndSet
            (P properties, C configuration) {

        int port = properties.getIntegerValue(SERVER_PORT, 8080);
        String host = properties.getStringValue(SERVER_HOST, "0.0.0.0");
        configuration.web(true).port(port).host(host);
    }

}
