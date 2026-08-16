/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * Configuration for the Debbie HTTP client, covering proxy, timeouts,
 * retry, authentication, caching and TLS settings.
 *
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientConfiguration implements DebbieConfiguration {
    /**
     * whether the HTTP client is enabled
     */
    private boolean enable;
    /**
     * configuration profile name
     */
    private String profile;
    /**
     * configuration category name
     */
    private String category;

    /**
     * proxy settings, or {@code null} if no proxy is configured
     */
    private HttpClientProxy proxy;

    /**
     * number of retry attempts on failure
     */
    private int retryTime;

    /**
     * connection timeout in milliseconds
     */
    private int connectTimeout;
    /**
     * read (socket) timeout in milliseconds
     */
    private int readTimeout;

    /**
     * response timeout in milliseconds
     */
    private int responseTimeout;

    /**
     * whether response caching is enabled
     */
    private boolean useCache;

    /**
     * basic-auth username
     */
    private String authUser;
    /**
     * basic-auth password
     */
    private String authPassword;

    /**
     * whether to skip TLS certificate verification (insecure mode)
     */
    private boolean insecure;

    /**
     * Creates a new default HTTP client configuration.
     */
    public HttpClientConfiguration() {
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    /**
     * Sets whether the HTTP client is enabled.
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the configuration profile name.
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String getCategory() {
        return category;
    }

    /**
     * Sets the configuration category name.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the proxy configuration, or {@code null} if none.
     */
    public HttpClientProxy getProxy() {
        return proxy;
    }

    /**
     * Sets the proxy configuration.
     */
    public void setProxy(HttpClientProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Returns {@code true} if a proxy is configured and enabled.
     */
    public boolean useProxy() {
        return proxy != null && proxy.useProxy();
    }

    /**
     * Returns the number of retry attempts on failure.
     */
    public int getRetryTime() {
        return retryTime;
    }

    /**
     * Sets the number of retry attempts on failure.
     */
    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    /**
     * Returns {@code true} if retry is enabled ({@code retryTime > 0}).
     */
    public boolean retry() {
        return retryTime > 0;
    }

    /**
     * Returns the connection timeout in milliseconds.
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connection timeout in milliseconds.
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Returns the basic-auth username.
     */
    public String getAuthUser() {
        return authUser;
    }

    /**
     * Sets the basic-auth username.
     */
    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    /**
     * Returns the basic-auth password.
     */
    public String getAuthPassword() {
        return authPassword;
    }

    /**
     * Sets the basic-auth password.
     */
    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    /**
     * Returns {@code true} if both auth user and password are non-blank.
     */
    public boolean needAuth() {
        return authUser != null && !authUser.isBlank()
                && authPassword != null && !authPassword.isBlank();
    }

    /**
     * Returns whether TLS certificate verification is skipped.
     */
    public boolean isInsecure() {
        return insecure;
    }

    /**
     * Sets whether to skip TLS certificate verification (insecure mode).
     */
    public void setInsecure(boolean insecure) {
        this.insecure = insecure;
    }

    /**
     * Returns the response timeout in milliseconds.
     */
    public int getResponseTimeout() {
        return responseTimeout;
    }

    /**
     * Sets the response timeout in milliseconds.
     */
    public void setResponseTimeout(int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    /**
     * Returns the read timeout in milliseconds.
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets the read timeout in milliseconds.
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Returns whether response caching is enabled.
     */
    public boolean isUseCache() {
        return useCache;
    }

    /**
     * Sets whether response caching is enabled.
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    /**
     * Creates a deep copy of this configuration, including a copy of the
     * {@link HttpClientProxy} if present.
     *
     * @return a new independent configuration instance
     */
    @SuppressWarnings("unchecked")
    @Override
    public HttpClientConfiguration copy() {
        var copy = new HttpClientConfiguration();
        copy.enable = this.enable;
        copy.profile = this.profile;
        copy.category = this.category;
        copy.retryTime = this.retryTime;
        copy.connectTimeout = this.connectTimeout;
        copy.readTimeout = this.readTimeout;
        copy.responseTimeout = this.responseTimeout;
        copy.useCache = this.useCache;
        copy.authUser = this.authUser;
        copy.authPassword = this.authPassword;
        copy.insecure = this.insecure;
        if (this.proxy != null) {
            var proxyCopy = new HttpClientProxy();
            proxyCopy.setProxyHost(this.proxy.getProxyHost());
            proxyCopy.setProxyPort(this.proxy.getProxyPort());
            proxyCopy.setUser(this.proxy.getUser());
            proxyCopy.setPassword(this.proxy.getPassword());
            copy.proxy = proxyCopy;
        }
        return copy;
    }

    @Override
    public void close() {
        // do nothing
    }
}
