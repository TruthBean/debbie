/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.netty;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.server.BaseServerProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/30 22:37.
 */
public class NettyProperties extends BaseServerProperties<NettyConfiguration> {

    private final Map<String, Map<String, NettyConfiguration>> configurationMap = new HashMap<>();

    public static final String ENABLE_KEY = "debbie.netty.enable";

    @Override
    public Map<String, Map<String, NettyConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return configurationMap;
    }

    @Override
    public Set<String> getCategories(String profile) {
        return configurationMap.get(profile).keySet();
    }

    @Override
    public Set<String> getCategories() {
        return configurationMap.keySet();
    }

    @Override
    public NettyConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        if (!StringUtils.hasText(profile)) {
            profile = getDefaultProfile();
        }
        if (!StringUtils.hasText(category)) {
            category = DEFAULT_CATEGORY;
        }
        if (configurationMap.isEmpty()) {
            buildConfiguration(applicationContext);
        }
        if (configurationMap.isEmpty()) {
            Map<String, NettyConfiguration> map = new HashMap<>();
            map.put(DEFAULT_CATEGORY, new NettyConfiguration(applicationContext.getClassLoader()));
            configurationMap.put(getDefaultProfile(), map);
        }
        return configurationMap.get(profile).get(category);
    }

    private void buildConfiguration(ApplicationContext applicationContext) {
        Set<String> profiles = super.getProfiles();
        for (String profile : profiles) {
            Environment environment = super.getEnvironmentIfPresent(profile);
            var classLoader = applicationContext.getClassLoader();

            NettyConfiguration configuration = new NettyConfiguration(classLoader);

            // BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
            // MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
            // configuration.copyFrom(mvcConfiguration);
            // configuration.copyFrom(beanConfiguration);

            NettyProperties properties = new NettyProperties();
            properties.loadAndSet(profile, properties, configuration);

            // configuration.setEnable(getBooleanValue(ENABLE_KEY, true));

            Map<String, NettyConfiguration> map = new HashMap<>();
            map.put(DEFAULT_CATEGORY, configuration);
            configurationMap.put(profile, map);
        }
    }

    @Override
    public void close() throws IOException {
        configurationMap.clear();
    }
}
