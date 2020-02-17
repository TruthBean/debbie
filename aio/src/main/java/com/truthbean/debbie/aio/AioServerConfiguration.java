package com.truthbean.debbie.aio;

import com.truthbean.debbie.server.AbstractServerConfiguration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:28
 */
public class AioServerConfiguration extends AbstractServerConfiguration {
    protected AioServerConfiguration(ClassLoader classLoader) {
        super(classLoader);
    }

    private String httpVersion;

    private String serverMessage;

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
}
