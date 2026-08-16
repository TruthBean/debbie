/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.debbie.concurrent.ThreadPoolConfiguration;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.server.AbstractServerConfiguration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for the AIO (NIO.2) HTTP server based on
 * {@link java.nio.channels.AsynchronousSocketChannel}.
 * <p>
 * Holds server-level settings such as HTTP version, character encoding,
 * connection timeout and an embedded {@link ThreadPoolConfiguration}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:28
 */
public class AioServerConfiguration extends AbstractServerConfiguration {

    /** whether the AIO server is enabled */
    private boolean enable;

    /** HTTP protocol version, defaults to {@code "1.1"} */
    private String httpVersion = "1.1";

    /** server banner message sent in response headers */
    private String serverMessage;

    /** character encoding for request/response, defaults to UTF-8 */
    private Charset charset = StandardCharsets.UTF_8;

    /** connection idle timeout in milliseconds, defaults to 5000 */
    private long connectionTimeout = 5000;

    /** whether to skip response encoding */
    private boolean ignoreEncode;

    /** thread pool configuration for the AIO server worker threads */
    private ThreadPoolConfiguration threadPoolConfig = new ThreadPoolConfiguration();

    /**
     * Creates a disabled AIO server configuration.
     *
     * @param classLoader the class loader used for resource loading
     */
    protected AioServerConfiguration(ClassLoader classLoader) {
    }

    /**
     * Creates an AIO server configuration with the specified enable state.
     *
     * @param classLoader the class loader used for resource loading
     * @param enable      whether the AIO server is enabled
     */
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

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
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

    /**
     * Fills in fields that are null or zero with values from the given
     * default configuration.
     *
     * @param defaultConfiguration the source of default values
     */
    void setDefaultIfNull(AioServerConfiguration defaultConfiguration) {
        if (httpVersion == null && defaultConfiguration.httpVersion != null) {
            httpVersion = defaultConfiguration.httpVersion;
        }
        if (serverMessage == null && defaultConfiguration.serverMessage != null) {
            serverMessage = defaultConfiguration.serverMessage;
        }
        if (charset == null && defaultConfiguration.charset != null) {
            charset = defaultConfiguration.charset;
        }
        if (connectionTimeout == 0L && defaultConfiguration.connectionTimeout != 0L) {
            connectionTimeout = defaultConfiguration.connectionTimeout;
        }
    }

    public ThreadPoolConfiguration getThreadPoolConfig() {
        return threadPoolConfig;
    }

    public void setThreadPoolConfig(ThreadPoolConfiguration threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }
}
