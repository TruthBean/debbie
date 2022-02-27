/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.server.BaseServerProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:38
 */
public class AioServerProperties extends BaseServerProperties<AioServerConfiguration> {

    private final Map<String, AioServerConfiguration> configurationMap = new HashMap<>();

    private static final String AIO_SERVER_PREFIX = "debbie.server.aio.";

    private static final String ENABLE = ".enable";
    private static final String HTTP_VERSION = ".http.version";
    private static final String SERVER_MESSAGE = ".message";
    private static final String CONNECTION_TIMEOUT = ".connection.timeout";
    private static final String URL_ENCODED = ".url.encoded";

    // ===========================================================================
    static final String ENABLE_KEY = "debbie.server.aio.enable";
    private static final String HTTP_VERSION_KEY = "debbie.server.aio.http.version";
    private static final String SERVER_MESSAGE_KEY = "debbie.server.aio.message";
    private static final String CONNECTION_TIMEOUT_KEY = "debbie.server.aio.connection.timeout";
    private static final String URL_ENCODED_KEY = "debbie.server.aio.url.encoded";
    // ===========================================================================


    @Override
    public Set<String> getProfiles() {
        return configurationMap.keySet();
    }

    @Override
    public AioServerConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (StringUtils.hasText(name)) {
            if (configurationMap.isEmpty()) {
                getConfiguration(applicationContext);
            }
            return configurationMap.get(name);
        } else {
            return getConfiguration(applicationContext);
        }
    }

    @Override
    public AioServerConfiguration getConfiguration(ApplicationContext applicationContext) {
        if (configurationMap.isEmpty()) {
            buildConfiguration(applicationContext);
        }
        if (configurationMap.isEmpty()) {
            var configuration = new AioServerConfiguration(applicationContext.getClassLoader());
            super.loadAndSet(this, configuration);
            configurationMap.put(DEFAULT_PROFILE, configuration);
        }
        return configurationMap.get(DEFAULT_PROFILE);
    }

    private void buildConfiguration(ApplicationContext applicationContext) {
        Map<String, String> aioProperties = getMatchedKey(AIO_SERVER_PREFIX);
        var classLoader = applicationContext.getClassLoader();

        aioProperties.forEach((k, v) -> {
            if (!k.equals(ENABLE_KEY) && k.endsWith(ENABLE)) {
                var startIndex = AIO_SERVER_PREFIX.length();
                var endIndex = k.length() - ENABLE.length();
                String name = k.substring(startIndex, endIndex);
                AioServerConfiguration configuration = getConfiguration(name, classLoader);
                boolean enable = getBoolean(v, false);
                configuration.setEnable(enable);
            } else if (k.equals(ENABLE_KEY)) {
                AioServerConfiguration configuration = getConfiguration(DEFAULT_PROFILE, classLoader);
                boolean enable = getBoolean(v, false);
                configuration.setEnable(enable);
            }
            if (!k.equals(HTTP_VERSION_KEY) && k.endsWith(HTTP_VERSION)) {
                var startIndex = AIO_SERVER_PREFIX.length();
                var endIndex = k.length() - HTTP_VERSION.length();
                String name = k.substring(startIndex, endIndex);
                AioServerConfiguration configuration = getConfiguration(name, classLoader);
                String httpVersion = getStringValue(v, "1.1");
                configuration.setHttpVersion(httpVersion);
            } else if (k.equals(HTTP_VERSION_KEY)) {
                AioServerConfiguration configuration = getConfiguration(DEFAULT_PROFILE, classLoader);
                String httpVersion = getStringValue(v, "1.1");
                configuration.setHttpVersion(httpVersion);
            }
            if (!k.equals(SERVER_MESSAGE_KEY) && k.endsWith(SERVER_MESSAGE)) {
                var startIndex = AIO_SERVER_PREFIX.length();
                var endIndex = k.length() - SERVER_MESSAGE.length();
                String name = k.substring(startIndex, endIndex);
                AioServerConfiguration configuration = getConfiguration(name, classLoader);
                String httpVersion = getStringValue(v, "A Simple Java Aio WebServer by Debbie Framework");
                configuration.setHttpVersion(httpVersion);
            } else if (k.equals(SERVER_MESSAGE_KEY)) {
                AioServerConfiguration configuration = getConfiguration(DEFAULT_PROFILE, classLoader);
                String httpVersion = getStringValue(v, "A Simple Java Aio WebServer by Debbie Framework");
                configuration.setHttpVersion(httpVersion);
            }
            if (!k.equals(CONNECTION_TIMEOUT_KEY) && k.endsWith(CONNECTION_TIMEOUT)) {
                var startIndex = AIO_SERVER_PREFIX.length();
                var endIndex = k.length() - CONNECTION_TIMEOUT.length();
                String name = k.substring(startIndex, endIndex);
                AioServerConfiguration configuration = getConfiguration(name, classLoader);
                long connectionTimeout = getLongValue(v, 5000L);
                configuration.setConnectionTimeout(connectionTimeout);
            } else if (k.equals(CONNECTION_TIMEOUT_KEY)) {
                AioServerConfiguration configuration = getConfiguration(DEFAULT_PROFILE, classLoader);
                long connectionTimeout = getLongValue(v, 5000L);
                configuration.setConnectionTimeout(connectionTimeout);
            }
            if (!k.equals(URL_ENCODED_KEY) && k.endsWith(URL_ENCODED)) {
                var startIndex = AIO_SERVER_PREFIX.length();
                var endIndex = k.length() - URL_ENCODED.length();
                String name = k.substring(startIndex, endIndex);
                AioServerConfiguration configuration = getConfiguration(name, classLoader);
                boolean encoded = getBoolean(v, true);
                configuration.setIgnoreEncode(!encoded);
            } else if (k.equals(URL_ENCODED_KEY)) {
                AioServerConfiguration configuration = getConfiguration(DEFAULT_PROFILE, classLoader);
                boolean encoded = getBoolean(v, true);
                configuration.setIgnoreEncode(!encoded);
            }
        });
    }

    public static boolean enableAio(EnvironmentContent envContent) {
        return envContent.getBooleanValue(ENABLE_KEY, true);
    }

    private AioServerConfiguration getConfiguration(String name, ClassLoader classLoader) {
        AioServerConfiguration configuration;
        if (configurationMap.containsKey(name)) {
            configuration = configurationMap.get(name);
        } else {
            configuration = new AioServerConfiguration(classLoader);
            super.loadAndSet(this, configuration);

            BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
            MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
            configuration.copyFrom(mvcConfiguration);
            configuration.copyFrom(beanConfiguration);
            configuration.setName(name);
            configurationMap.put(name, configuration);
        }
        return configuration;
    }

    @Override
    public void close() throws IOException {
        configurationMap.clear();
    }
}
