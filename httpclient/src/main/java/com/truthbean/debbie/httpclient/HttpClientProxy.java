package com.truthbean.debbie.httpclient;

/**
 * Proxy configuration for the HTTP client, including host, port and
 * optional basic authentication credentials.
 *
 * @author TruthBean
 * @since 0.5.1
 */
public class HttpClientProxy {
    /** proxy server host */
    private String proxyHost;
    /** proxy server port */
    private int proxyPort;
    /** proxy basic-auth username */
    private String user;
    /** proxy basic-auth password */
    private String password;

    /** Returns {@code true} if both proxy user and password are non-blank. */
    public boolean needAuth() {
        return user != null && !user.isBlank()
                && password != null && !password.isBlank();
    }

    /** Returns the proxy server host. */
    public String getProxyHost() {
        return proxyHost;
    }

    /** Sets the proxy server host. */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /** Returns the proxy server port. */
    public int getProxyPort() {
        return proxyPort;
    }

    /** Sets the proxy server port. */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /** Returns the proxy basic-auth username. */
    public String getUser() {
        return user;
    }

    /** Sets the proxy basic-auth username. */
    public void setUser(String user) {
        this.user = user;
    }

    /** Returns the proxy basic-auth password. */
    public String getPassword() {
        return password;
    }

    /** Sets the proxy basic-auth password. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Returns {@code true} if a proxy host and valid port are configured. */
    public boolean useProxy() {
        return proxyHost != null && !proxyHost.isBlank() && proxyPort > 0;
    }
}
