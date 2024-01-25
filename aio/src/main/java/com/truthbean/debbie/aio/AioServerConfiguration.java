/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.server.AbstractServerConfiguration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:28
 */
public class AioServerConfiguration extends AbstractServerConfiguration {

    private boolean enable;

    private String httpVersion = "1.1";

    private String serverMessage;

    private long connectionTimeout;

    private boolean ignoreEncode;

    protected AioServerConfiguration(ClassLoader classLoader) {
    }

    protected AioServerConfiguration(ClassLoader classLoader, boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public String getProfile() {
        return null;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public <T extends DebbieConfiguration> T copy() {
        return null;
    }

    @Override
    public void close() {

    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isIgnoreEncode() {
        return ignoreEncode;
    }

    public void setIgnoreEncode(boolean ignoreEncode) {
        this.ignoreEncode = ignoreEncode;
    }

    void setDefaultIfNull(AioServerConfiguration defaultConfiguration) {
        if (httpVersion == null && defaultConfiguration.httpVersion != null) {
            httpVersion = defaultConfiguration.httpVersion;
        }
        if (serverMessage == null && defaultConfiguration.serverMessage != null) {
            serverMessage = defaultConfiguration.serverMessage;
        }
        if (connectionTimeout == 0L && defaultConfiguration.connectionTimeout != 0L) {
            connectionTimeout = defaultConfiguration.connectionTimeout;
        }
    }
}
