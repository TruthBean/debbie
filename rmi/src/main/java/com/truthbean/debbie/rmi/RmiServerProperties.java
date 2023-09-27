/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rmi;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RmiServerProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<RmiServerConfiguration> {

    private final RmiServerConfiguration configuration;

    public static final String ENABLE_KEY = "debbie.rmi.enable";
    //=================================================================================================================
    private static final String RMI_SERVER_HOST = "debbie.rmi.server.host";
    private static final String RMI_SERVER_PORT = "debbie.rmi.server.port";

    //=================================================================================================================

    public RmiServerProperties() {
        configuration = new RmiServerConfiguration();
        configuration.setRmiBindAddress(getStringValue(RMI_SERVER_HOST, "localhost"));
        configuration.setRmiBindPort(getIntegerValue(RMI_SERVER_PORT, 8040));
    }

    @Override
    public Map<String, Map<String, RmiServerConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return null;
    }

    @Override
    public Set<String> getCategories(String profile) {
        return null;
    }

    @Override
    public RmiServerConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return null;
    }

    public RmiServerConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (getDefaultProfile().equals(name)) {
            return configuration;
        }
        return null;
    }

    @Override
    public RmiServerConfiguration getConfiguration(ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public void close() throws IOException {

    }
}
