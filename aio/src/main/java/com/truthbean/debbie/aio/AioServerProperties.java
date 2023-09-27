/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.server.BaseServerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:38
 */
public class AioServerProperties extends BaseServerProperties<AioServerConfiguration> {

    private final Map<String, Map<String, AioServerConfiguration>> configurationMap = new HashMap<>();

    private static final String AIO_SERVER_PREFIX = "debbie.server.aio.";
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

    public AioServerProperties(final ApplicationContext applicationContext) {
        getConfiguration(applicationContext);
    }

    @Override
    public Map<String, Map<String, AioServerConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return configurationMap;
    }

    @Override
    public Set<String> getCategories(final String profile) {
        return configurationMap.get(profile).keySet();
    }

    @Override
    public AioServerConfiguration getConfiguration(final String profile, final String category,
                                                   final ApplicationContext applicationContext) {
        if (StringUtils.hasText(profile)) {
            if (configurationMap.isEmpty() || !configurationMap.containsKey(profile) || configurationMap.get(profile).isEmpty()) {
                getConfiguration(applicationContext);
            }
            return configurationMap.get(profile).get(category);
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
            super.loadAndSet(DEFAULT_CATEGORY, this, configuration);
            Map<String, AioServerConfiguration> map = new HashMap<>();
            map.put(DEFAULT_CATEGORY, configuration);
            configurationMap.put(getDefaultProfile(), map);
        }
        return configurationMap.get(getDefaultProfile()).get(DEFAULT_CATEGORY);
    }

    private void buildConfiguration(ApplicationContext applicationContext) {
        Set<String> profiles = getProfiles();
        for (String profile : profiles) {
            Environment environment = getEnvironmentIfPresent(profile);
            Map<String, String> aioProperties = environment.getMatchedKey(AIO_SERVER_PREFIX);
            var classLoader = applicationContext.getClassLoader();

            aioProperties.forEach((k, v) -> {
                if (k.equals(ENABLE_KEY)) {
                    AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                    boolean enable = getBoolean(v, false);
                    configuration.setEnable(enable);
                }
                if (!k.equals(HTTP_VERSION_KEY) && k.endsWith(HTTP_VERSION)) {
                    var startIndex = AIO_SERVER_PREFIX.length();
                    var endIndex = k.length() - HTTP_VERSION.length();
                    String category = k.substring(startIndex, endIndex);
                    AioServerConfiguration configuration = getConfiguration(profile, category, classLoader);
                    String httpVersion = getStringValue(v, "1.1");
                    configuration.setHttpVersion(httpVersion);
                } else if (k.equals(HTTP_VERSION_KEY)) {
                    AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                    String httpVersion = getStringValue(v, "1.1");
                    configuration.setHttpVersion(httpVersion);
                }
                if (!k.equals(SERVER_MESSAGE_KEY) && k.endsWith(SERVER_MESSAGE)) {
                    var startIndex = AIO_SERVER_PREFIX.length();
                    var endIndex = k.length() - SERVER_MESSAGE.length();
                    String name = k.substring(startIndex, endIndex);
                    AioServerConfiguration configuration = getConfiguration(profile, name, classLoader);
                    String httpVersion = getStringValue(v, "A Simple Java Aio WebServer by Debbie Framework");
                    configuration.setHttpVersion(httpVersion);
                } else if (k.equals(SERVER_MESSAGE_KEY)) {
                    AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                    String httpVersion = getStringValue(v, "A Simple Java Aio WebServer by Debbie Framework");
                    configuration.setHttpVersion(httpVersion);
                }
                if (!k.equals(CONNECTION_TIMEOUT_KEY) && k.endsWith(CONNECTION_TIMEOUT)) {
                    var startIndex = AIO_SERVER_PREFIX.length();
                    var endIndex = k.length() - CONNECTION_TIMEOUT.length();
                    String name = k.substring(startIndex, endIndex);
                    AioServerConfiguration configuration = getConfiguration(profile, name, classLoader);
                    long connectionTimeout = getLongValue(v, 5000L);
                    configuration.setConnectionTimeout(connectionTimeout);
                } else if (k.equals(CONNECTION_TIMEOUT_KEY)) {
                    AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                    long connectionTimeout = getLongValue(v, 5000L);
                    configuration.setConnectionTimeout(connectionTimeout);
                }
                if (!k.equals(URL_ENCODED_KEY) && k.endsWith(URL_ENCODED)) {
                    var startIndex = AIO_SERVER_PREFIX.length();
                    var endIndex = k.length() - URL_ENCODED.length();
                    String name = k.substring(startIndex, endIndex);
                    AioServerConfiguration configuration = getConfiguration(profile, name, classLoader);
                    boolean encoded = getBoolean(v, true);
                    configuration.setIgnoreEncode(!encoded);
                } else if (k.equals(URL_ENCODED_KEY)) {
                    AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                    boolean encoded = getBoolean(v, true);
                    configuration.setIgnoreEncode(!encoded);
                }
            });
        }
    }

    public static boolean enableAio(Environment environment) {
        return environment.getBooleanValue(ENABLE_KEY, true);
    }

    private AioServerConfiguration getConfiguration(String profile, String category, ClassLoader classLoader) {
        AioServerConfiguration configuration;
        Map<String, AioServerConfiguration> map;
        if (configurationMap.containsKey(profile)) {
            map = configurationMap.get(profile);
        } else {
            map = new HashMap<>();
            configurationMap.put(profile, map);
        }
        if (map.containsKey(category)) {
            configuration = map.get(category);
        } else {
            configuration = new AioServerConfiguration(classLoader);
            super.loadAndSet(category, this, configuration);

            // BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
            // MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
            // configuration.copyFrom(mvcConfiguration);
            // configuration.copyFrom(beanConfiguration);
            // configuration.setProfile(profile);
            // configuration.setCategory(category);
            map.put(category, configuration);
        }
        return configuration;
    }

    @Override
    public void close() {
        configurationMap.clear();
    }
}
