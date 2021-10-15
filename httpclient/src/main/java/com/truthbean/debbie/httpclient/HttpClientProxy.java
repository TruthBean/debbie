package com.truthbean.debbie.httpclient;

/**
 * @author TruthBean
 * @since 0.5.1
 */
public class HttpClientProxy {
    private String proxyHost;
    private int proxyPort;
    private String user;
    private String password;

    public boolean needAuth() {
        return user != null && !user.isBlank()
                && password != null && !password.isBlank();
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean useProxy() {
        return proxyHost != null && !proxyHost.isBlank() && proxyPort > 0;
    }
}
