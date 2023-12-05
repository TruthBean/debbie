/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.environment.NoCategoryConfigurationException;
import com.truthbean.debbie.environment.NoProfiledEnvironmentException;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.properties.CategoriedProperties;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-26 16:41
 */
public class DefaultDataSourcePoolProperties extends CategoriedProperties<DefaultDataSourcePoolConfiguration> {

    private final boolean unpool;

    //=====================================================================================================================================
    private static final String POOL_KEY_PREFIX = "debbie.datasource.pool.";
    private static final String MAX_ACTIVE_CONNECTION = "max-active-connection";
    private static final String MAX_IDLE_CONNECTION = "max-idle-connection";
    private static final String MAX_CHECKOUT_TIME = "max-checkout-time";
    private static final String TIME_TO_WAIT = "time-to-wait";
    private static final String MAX_LOCAL_BAD_CONNECTION_TOLERANCE = "max-local-bad-connection-tolerance";
    private static final String PING_QUERY = "ping-query";
    private static final String PING_CONNECTION_NOT_USED_FOR = "ping-connection-not-used-for";
    //=====================================================================================================================================

    private static final String MAX_ACTIVE_CONNECTION_KEY = "debbie.datasource.pool.max-active-connection";
    private static final String MAX_IDLE_CONNECTION_KEY = "debbie.datasource.pool.max-idle-connection";
    private static final String MAX_CHECKOUT_TIME_KEY = "debbie.datasource.pool.max-checkout-time";
    private static final String TIME_TO_WAIT_KEY = "debbie.datasource.pool.time-to-wait";
    private static final String MAX_LOCAL_BAD_CONNECTION_TOLERANCE_KEY = "debbie.datasource.pool.max-local-bad-connection-tolerance";
    private static final String PING_QUERY_KEY = "debbie.datasource.pool.ping-query";
    private static final String PING_CONNECTION_NOT_USED_FOR_KEY = "debbie.datasource.pool.ping-connection-not-used-for";
    //===========================================================================

    private DataSourceProperties dataSourceProperties;
    private static DefaultDataSourcePoolConfiguration configuration;
    private final Map<String, Map<String, DefaultDataSourcePoolConfiguration>> configurationMap = new HashMap<>();

    public DefaultDataSourcePoolProperties(ApplicationContext applicationContext) {
        super();
        if (getMatchedKey(POOL_KEY_PREFIX).isEmpty()) {
            unpool = true;
            dataSourceProperties = new DataSourceProperties(applicationContext);
        } else {
            unpool = false;
            DataSourceConfiguration dataSourceConfiguration = new DataSourceProperties(applicationContext).getDefaultConfiguration();
            configuration = new DefaultDataSourcePoolConfiguration(dataSourceConfiguration);
            configuration.setMaxActiveConnection(getIntegerValue(MAX_ACTIVE_CONNECTION_KEY, 10));
            configuration.setMaxIdleConnection(getIntegerValue(MAX_IDLE_CONNECTION_KEY, 5));
            configuration.setMaxCheckoutTime(getIntegerValue(MAX_CHECKOUT_TIME_KEY, 20000));
            configuration.setTimeToWait(getIntegerValue(TIME_TO_WAIT_KEY, 20000));
            configuration.setMaxLocalBadConnectionTolerance(getIntegerValue(MAX_LOCAL_BAD_CONNECTION_TOLERANCE_KEY, 3));
            String pingQuery = getValue(PING_QUERY_KEY);
            if (pingQuery == null || pingQuery.isBlank()) {
                configuration.setPingEnabled(false);
            } else {
                configuration.setPingQuery(pingQuery);
                configuration.setPingEnabled(true);
                configuration.setPingConnectionNotUsedFor(getIntegerValue(PING_CONNECTION_NOT_USED_FOR_KEY, 0));
                configuration.setDataSourceFactoryClass(DefaultDataSourcePoolFactory.class);
            }
            Map<String, DefaultDataSourcePoolConfiguration> map;
            if (configurationMap.containsKey(getDefaultProfile())) {
                map = configurationMap.get(getDefaultProfile());
            } else {
                map = new HashMap<>();
            }
            map.put(DEFAULT_CATEGORY, configuration);
        }
    }

    @Override
    public String getKeyPrefix() {
        return POOL_KEY_PREFIX;
    }

    @Override
    public Map<String, Map<String, DefaultDataSourcePoolConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return configurationMap;
    }

    @Override
    public Set<String> getCategories(String profile) {
        if (unpool) {
            return dataSourceProperties.getCategories(profile);
        }
        return configurationMap.get(profile).keySet();
    }

    @Override
    public DefaultDataSourcePoolConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        if (!StringUtils.hasText(profile)) {
            profile = getDefaultProfile();
        }
        if (!StringUtils.hasText(category)) {
            category = DEFAULT_CATEGORY;
        }
        /*if (!configurationMap.containsKey(profile)) {
            throw new NoProfiledEnvironmentException("No profile " + profile + "!");
        }*/
        if (!configurationMap.containsKey(profile) || !configurationMap.get(profile).containsKey(category)) {
            throw new NoCategoryConfigurationException("Profile " + profile + " Environment has no category " + category + "!");
        }
        return configurationMap.get(profile).get(category);
    }

    public DefaultDataSourcePoolConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (DEFAULT_CATEGORY.equals(name)) {
            return getConfiguration(applicationContext);
        }
        return configurationMap.get(getDefaultProfile()).get(name);
    }

    @Override
    public DefaultDataSourcePoolConfiguration getConfiguration(ApplicationContext applicationContext) {
        DataSourceConfiguration dataSourceConfiguration = new DataSourceProperties(applicationContext).getDefaultConfiguration();
        if (unpool) {
            return new DefaultDataSourcePoolConfiguration(dataSourceConfiguration);
        }
        return toConfiguration(dataSourceConfiguration);
    }

    public static DefaultDataSourcePoolConfiguration toConfiguration(DataSourceConfiguration dataSourceConfiguration) {
        if (configuration == null) {
            configuration = new DefaultDataSourcePoolConfiguration(dataSourceConfiguration);
        }
        return configuration;
    }

    @Override
    public void close() throws Exception {
        configurationMap.forEach((key, map) -> map.clear());
        configurationMap.clear();
        configuration = null;
    }
}
