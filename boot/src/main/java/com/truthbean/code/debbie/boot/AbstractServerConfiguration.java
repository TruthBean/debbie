package com.truthbean.code.debbie.boot;

import com.truthbean.code.debbie.mvc.MvcConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractServerConfiguration extends MvcConfiguration {

    private int port = 8080;
    private String host = "localhost";
    private boolean web = false;

    protected AbstractServerConfiguration() {
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
            boolean illegal = (host == null || "".equals(host)) || port <= 0;
            if (illegal) {
                throw new RuntimeException("host is null or port is wrong");
            }
        }
    }

}