package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientProperties extends BaseProperties implements DebbieProperties {
    private static final HttpClientConfiguration configuration = new HttpClientConfiguration();

    //===========================================================================
    private static final String PROXY_HOST_KEY = "debbie.httpclient.proxy.host";
    private static final String PROXY_PORT_KEY = "debbie.httpclient.proxy.port";
    private static final String RETRY_TIME_KEY = "debbie.httpclient.retry-time";
    private static final String CONNECTION_TIMEOUT_KEY = "debbie.httpclient.connection-timeout";
    private static final String READ_TIMEOUT_KEY = "debbie.httpclient.read-timeout";
    private static final String RESPONSE_TIMEOUT_KEY = "debbie.httpclient.response-timeout";
    private static final String BASIC_AUTH_USER_KEY = "debbie.httpclient.auth.basic.user";
    private static final String BASIC_AUTH_PASSWORD_KEY = "debbie.httpclient.auth.basic.password";
    private static final String INSECURE_KEY = "debbie.httpclient.insecure";
    //===========================================================================


    public HttpClientProperties() {
        configuration.setProxyHost(getValue(PROXY_HOST_KEY));
        configuration.setProxyPort(getIntegerValue(PROXY_PORT_KEY, 0));
        configuration.setRetryTime(getIntegerValue(RETRY_TIME_KEY, 0));

        configuration.setConnectTimeout(getIntegerValue(CONNECTION_TIMEOUT_KEY, 10000));
        configuration.setResponseTimeout(getIntegerValue(RESPONSE_TIMEOUT_KEY, 10000));
        configuration.setReadTimeout(getIntegerValue(READ_TIMEOUT_KEY, 10000));

        configuration.setAuthUser(getValue(BASIC_AUTH_USER_KEY));
        configuration.setAuthPassword(getValue(BASIC_AUTH_PASSWORD_KEY));

        configuration.setInsecure(getBooleanValue(INSECURE_KEY, false));
    }

    public static HttpClientConfiguration toConfiguration() {
        return configuration;
    }

    public HttpClientConfiguration loadConfiguration() {
        return configuration;
    }

    @Override
    public HttpClientConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        return configuration;
    }
}
