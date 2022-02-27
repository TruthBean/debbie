/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.netty;

import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;
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

    private final Map<String, NettyConfiguration> configurationMap = new HashMap<>();

    public static final String ENABLE_KEY = "debbie.netty.enable";

    @Override
    public Set<String> getProfiles() {
        return configurationMap.keySet();
    }

    @Override
    public NettyConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (StringUtils.hasText(name)) {
            if (configurationMap.isEmpty()) {
                buildConfiguration(applicationContext);
            }
            return configurationMap.get(name);
        } else {
            return getConfiguration(applicationContext);
        }
    }

    @Override
    public NettyConfiguration getConfiguration(ApplicationContext applicationContext) {
        if (configurationMap.isEmpty()) {
            buildConfiguration(applicationContext);
        }
        if (configurationMap.isEmpty()) {
            configurationMap.put(DEFAULT_PROFILE, new NettyConfiguration(applicationContext.getClassLoader()));
        }
        return configurationMap.get(DEFAULT_PROFILE);
    }

    private void buildConfiguration(ApplicationContext applicationContext) {
        var classLoader = applicationContext.getClassLoader();

        NettyConfiguration configuration = new NettyConfiguration(classLoader);

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
        MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        NettyProperties properties = new NettyProperties();
        properties.loadAndSet(properties, configuration);

        configuration.setEnable(getBooleanValue(ENABLE_KEY, true));

        configurationMap.put(DEFAULT_PROFILE, configuration);
    }

    @Override
    public void close() throws IOException {
        configurationMap.clear();
    }
}
