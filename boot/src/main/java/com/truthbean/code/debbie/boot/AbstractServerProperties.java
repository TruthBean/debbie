package com.truthbean.code.debbie.boot;

import com.truthbean.code.debbie.core.properties.AbstractProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractServerProperties extends AbstractProperties {
    //===========================================================================
    private static final String SERVER_PORT = "debbie.server.port";
    private static final String SERVER_HOST = "debbie.server.host";

    //===========================================================================

    public <P extends AbstractServerProperties, C extends AbstractServerConfiguration> void loadAndSet
            (P properties, C configuration) {

        int port = properties.getIntegerValue(SERVER_PORT, 8080);
        String host = properties.getStringValue(SERVER_HOST, "0.0.0.0");
        configuration.web(true).port(port).host(host);
    }

}
