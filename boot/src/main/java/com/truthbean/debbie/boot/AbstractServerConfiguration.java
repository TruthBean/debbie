package com.truthbean.debbie.boot;

import com.truthbean.debbie.mvc.MvcConfiguration;

import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractServerConfiguration extends MvcConfiguration {

    private String name;

    private int port = 8080;
    private String host = "localhost";
    private boolean web = false;

    protected AbstractServerConfiguration() {
    }

    public String getName() {
        return name;
    }

    protected AbstractServerConfiguration name(String name) {
        this.name = name;
        return this;
    }

    public int getPort() {
        return port;
    }

    protected AbstractServerConfiguration port(int port) {
        this.port = port;
        return this;
    }

    public boolean isWeb() {
        return web;
    }

    protected AbstractServerConfiguration web(boolean web) {
        this.web = web;
        return this;
    }

    public String getHost() {
        return host;
    }

    protected AbstractServerConfiguration host(String host) {
        this.host = host;
        return this;
    }

    public void check() {
        if (web) {
            boolean illegal = (host == null || host.isBlank()) || port <= 0;
            if (illegal) {
                throw new RuntimeException("host is null or port is wrong");
            }
        }
    }

}
