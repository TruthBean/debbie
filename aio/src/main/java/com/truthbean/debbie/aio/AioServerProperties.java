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

import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:38
 */
public class AioServerProperties extends BaseServerProperties<AioServerConfiguration> {

    private final Map<String, Map<String, AioServerConfiguration>> configurationMap = new HashMap<>();

    private static final String AIO_SERVER_PREFIX = "debbie.server.aio.";
    private static final String HTTP_VERSION = "http.version";
    private static final String SERVER_MESSAGE = "message";
    private static final String CONNECTION_TIMEOUT = "connection.timeout";
    private static final String URL_ENCODED = "url.encoded";

    // ===========================================================================
    static final String ENABLE_KEY = "debbie.server.aio.enable";
    private static final String HTTP_VERSION_KEY = "debbie.server.aio.http.version";
    private static final String SERVER_MESSAGE_KEY = "debbie.server.aio.message";
    private static final String CONNECTION_TIMEOUT_KEY = "debbie.server.aio.connection.timeout";
    private static final String URL_ENCODED_KEY = "debbie.server.aio.url.encoded";
    // ===========================================================================

    public AioServerProperties(final ApplicationContext applicationContext) {
        setDefaultProfile(DEFAULT_PROFILE);
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

    private boolean isGlobalEnable() {
        return getBooleanValue(ENABLE_KEY, true);
    }

    private Set<String> getRawCategories() {
        Set<String> set = new HashSet<>();
        String[] arr = getStringArray(getValue(AIO_SERVER_PREFIX + CATEGORIES_KEY_NAME), ",");
        if (arr == null || arr.length == 0) {
            arr = getStringArray(getValue(AIO_SERVER_PREFIX + CATEGORIES_KEY_NAME), ";");
        }
        if (arr != null) {
            set.addAll(Set.of(arr));
        }
        set.add("");
        return set;
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
            var configuration = new AioServerConfiguration(applicationContext.getClassLoader(), true);
            super.loadAndSet(DEFAULT_CATEGORY, this, configuration);
            Map<String, AioServerConfiguration> map = new HashMap<>();
            map.put(DEFAULT_CATEGORY, configuration);
            configurationMap.put(getDefaultProfile(), map);
        }
        return configurationMap.get(getDefaultProfile()).get(DEFAULT_CATEGORY);
    }

    private void buildConfiguration(ApplicationContext applicationContext) {
        if (!isGlobalEnable()) {
            return;
        }
        Set<String> profiles = getProfiles();
        Set<String> categories = getRawCategories();
        for (String profile : profiles) {
            for (String category : categories) {
                buildConfiguration(applicationContext, profile, category + ".");
            }
            Map<String, AioServerConfiguration> map = configurationMap.get(profile);
            if (map != null && !map.isEmpty()) {
                AioServerConfiguration defaultAioServerConfiguration = map.get(DEFAULT_CATEGORY);
                for (String category : categories) {
                    if (!category.isBlank()) {
                        AioServerConfiguration aioServerConfiguration = map.get(DEFAULT_CATEGORY);
                        aioServerConfiguration.setDefaultIfNull(defaultAioServerConfiguration);
                    }
                }
            }
        }
    }

    private void buildConfiguration(ApplicationContext applicationContext, String profile, String category) {
        Environment environment = getEnvironmentIfPresent(profile);
        Map<String, String> aioProperties = environment.getMatchedKey(AIO_SERVER_PREFIX);
        var classLoader = applicationContext.getClassLoader();

        aioProperties.forEach((k, v) -> {
            String key = AIO_SERVER_PREFIX + category + ENABLE_KEY_NAME;
            if (k.equals(key)) {
                AioServerConfiguration configuration = getConfiguration(profile, category, classLoader);
                boolean enable = getBoolean(v, true);
                configuration.setEnable(enable);
            }
            key = AIO_SERVER_PREFIX + category + HTTP_VERSION;
            if (k.equals(key)) {
                AioServerConfiguration configuration = getConfiguration(profile, category, classLoader);
                String httpVersion = getStringValue(v, "1.1");
                configuration.setHttpVersion(httpVersion);
            }
            key = AIO_SERVER_PREFIX + category + SERVER_MESSAGE;
            if (k.endsWith(key)) {
                AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                String serverMessage = getStringValue(v, "A Simple Java Aio WebServer by Debbie Framework");
                configuration.setServerMessage(serverMessage);
            }
            key = AIO_SERVER_PREFIX + category + CONNECTION_TIMEOUT;
            if (k.equals(key)) {
                AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                long connectionTimeout = getLongValue(v, 5000L);
                configuration.setConnectionTimeout(connectionTimeout);
            }
            key = AIO_SERVER_PREFIX + category + URL_ENCODED;
            if (k.equals(key)) {
                AioServerConfiguration configuration = getConfiguration(profile, DEFAULT_CATEGORY, classLoader);
                boolean encoded = getBoolean(v, true);
                configuration.setIgnoreEncode(!encoded);
            }
        });
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
        if ("".equals(category)) {
            category = DEFAULT_CATEGORY;
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
