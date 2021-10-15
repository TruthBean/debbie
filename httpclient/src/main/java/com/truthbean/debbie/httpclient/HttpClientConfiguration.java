/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientConfiguration implements DebbieConfiguration {
    private HttpClientProxy proxy;

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

    private boolean useCache;

    /**
     * auth
     */
    private String authUser;
    private String authPassword;

    private boolean insecure;

    public HttpClientProxy getProxy() {
        return proxy;
    }

    public void setProxy(HttpClientProxy proxy) {
        this.proxy = proxy;
    }

    public boolean useProxy() {
        return proxy != null && proxy.useProxy();
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

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    @Override
    public void reset() {
        // do nothing
    }
}
