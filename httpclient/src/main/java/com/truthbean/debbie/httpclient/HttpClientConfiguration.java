package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientConfiguration implements DebbieConfiguration {
    private String proxyHost;
    private int proxyPort;

    /**
     * 重试次数
     */
    private int retryTime;

    /**
     * 连接超时时间，单位毫秒
     */
    private int connectTimeout;
    private int readTimeout;

    private int responseTimeout;

    /**
     * auth
     */
    private String authUser;
    private String authPassword;

    private boolean insecure;

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

    public boolean useProxy() {
        return proxyHost != null && !proxyHost.isBlank() && proxyPort > 0;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public boolean retry() {
        return retryTime > 0;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public boolean needAuth() {
        return authUser != null && !authUser.isBlank()
                && authPassword != null && !authPassword.isBlank();
    }

    public boolean isInsecure() {
        return insecure;
    }

    public void setInsecure(boolean insecure) {
        this.insecure = insecure;
    }

    public int getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
