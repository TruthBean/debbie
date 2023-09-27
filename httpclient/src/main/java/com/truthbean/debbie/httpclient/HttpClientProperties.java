/*
  Copyright (c) 2023 TruthBean(Rogar·Q)
  Debbie is licensed under Mulan PSL v2.
  You can use this software according to the terms and conditions of the Mulan PSL v2.
  You may obtain a copy of Mulan PSL v2 at:
          http://license.coscl.org.cn/MulanPSL2
  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
  See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.properties.DebbieProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<HttpClientConfiguration> {
    private final Map<String, Map<String, HttpClientConfiguration>> map = new HashMap<>();

    //===========================================================================
    private static final String PROXY_HOST_KEY = "debbie.httpclient.proxy.host";
    private static final String PROXY_PORT_KEY = "debbie.httpclient.proxy.port";
    private static final String PROXY_BASIC_AUTH_USE_KEY = "debbie.httpclient.proxy.auth.basic.user";
    private static final String PROXY_BASIC_AUTH_PASSWORD_KEY = "debbie.httpclient.proxy.auth.basic.password";
    private static final String RETRY_TIME_KEY = "debbie.httpclient.retry-time";
    private static final String CONNECTION_TIMEOUT_KEY = "debbie.httpclient.connection-timeout";
    private static final String READ_TIMEOUT_KEY = "debbie.httpclient.read-timeout";
    private static final String RESPONSE_TIMEOUT_KEY = "debbie.httpclient.response-timeout";
    private static final String BASIC_AUTH_USER_KEY = "debbie.httpclient.auth.basic.user";
    private static final String BASIC_AUTH_PASSWORD_KEY = "debbie.httpclient.auth.basic.password";
    private static final String INSECURE_KEY = "debbie.httpclient.insecure";
    //===========================================================================


    public HttpClientProperties() {
        Set<String> profiles = super.getProfiles();
        for (String profile : profiles) {
            Environment environment = super.getEnvironmentIfPresent(profile);
            HttpClientProxy proxy = new HttpClientProxy();
            proxy.setProxyHost(environment.getValue(PROXY_HOST_KEY));
            proxy.setProxyPort(environment.getIntegerValue(PROXY_PORT_KEY, 0));
            proxy.setUser(environment.getValue(PROXY_BASIC_AUTH_USE_KEY));
            proxy.setPassword(environment.getValue(PROXY_BASIC_AUTH_PASSWORD_KEY));
            HttpClientConfiguration configuration = new HttpClientConfiguration();
            configuration.setProfile(profile);
            configuration.setCategory(DEFAULT_CATEGORY);
            configuration.setProxy(proxy);

            configuration.setRetryTime(environment.getIntegerValue(RETRY_TIME_KEY, 0));

            configuration.setConnectTimeout(environment.getIntegerValue(CONNECTION_TIMEOUT_KEY, 10000));
            configuration.setResponseTimeout(environment.getIntegerValue(RESPONSE_TIMEOUT_KEY, 10000));
            configuration.setReadTimeout(environment.getIntegerValue(READ_TIMEOUT_KEY, 10000));

            configuration.setAuthUser(environment.getValue(BASIC_AUTH_USER_KEY));
            configuration.setAuthPassword(environment.getValue(BASIC_AUTH_PASSWORD_KEY));

            configuration.setInsecure(environment.getBooleanValue(INSECURE_KEY, false));
            Map<String, HttpClientConfiguration> map = new HashMap<>();
            map.put(DEFAULT_CATEGORY, configuration);
            this.map.put(profile, map);
        }
    }

    @Override
    public Map<String, Map<String, HttpClientConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return map;
    }

    @Override
    public Set<String> getCategories(String profile) {
        return map.get(profile).keySet();
    }

    @Override
    public HttpClientConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        if (!StringUtils.hasText(profile)) {
            profile = getDefaultProfile();
        }
        if (!StringUtils.hasText(category)) {
            category = DEFAULT_CATEGORY;
        }
        return map.get(profile).get(category);
    }

    public HttpClientConfiguration loadConfiguration() {
        return map.get(getDefaultProfile()).get(DEFAULT_CATEGORY);
    }

    public HttpClientConfiguration getDefaultConfiguration() {
        return map.get(getDefaultProfile()).get(DEFAULT_CATEGORY);
    }

    @Override
    public void close() throws IOException {
        map.forEach((profile, m) -> {
            m.forEach((category, config) -> config.close());
            m.clear();
        });
        map.clear();
    }
}
