/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.core.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:49.
 */
public class DataSourceProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<DataSourceConfiguration> {
    private final Map<String, Map<String, DataSourceConfiguration>> configurationMap = new HashMap<>();

    protected static final String DATASOURCE_KEY_PREFIX = "debbie.datasource.";

    private static final String DRIVER_NAME = "driver-name";
    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    private static final String AUTO_COMMIT = "auto-commit";
    private static final String LEVEL = "default-transaction-isolation-level";
    private static final String DRIVER = "driver.";
    private static final String FACTORY = "factory";
    //=================================================================================================================
    private static final String DRIVER_NAME_KEY = "debbie.datasource.driver-name";
    private static final String URL_KEY = "debbie.datasource.url";
    private static final String USER_KEY = "debbie.datasource.user";
    private static final String PASSWORD_KEY = "debbie.datasource.password";

    private static final String AUTO_COMMIT_KEY = "debbie.datasource.auto-commit";
    private static final String DEFAULT_TRANSACTION_ISOLATION_LEVEL = "debbie.datasource.default-transaction-isolation-level";

    private static final String DRIVER_PROPERTIES_PREFIX = "debbie.datasource.driver.";

    private static final String DATA_SOURCE_FACTORY = "debbie.datasource.factory";
    //=================================================================================================================

    public DataSourceProperties(ApplicationContext applicationContext) {
        Set<String> profiles = getProfiles();
        for (String profile : profiles) {
            applicationContext.getEnvironmentHolder()
                    .getEnvironment(profile)
                    .ifPresent(environment -> {
                        DataSourceConfiguration defaultConfiguration = new DataSourceConfiguration();
                        setConfiguration(defaultConfiguration, environment, profile, DEFAULT_CATEGORY, "");
                        Set<String> categories = getRawCategories();
                        for (String category : categories) {
                            DataSourceConfiguration configuration = new DataSourceConfiguration(defaultConfiguration);
                            setConfiguration(configuration, environment, profile, category, category + ".");
                        }
                    });
        }
    }

    private Set<String> getRawCategories() {
        Set<String> set = new HashSet<>();
        String[] arr = getStringArray(getValue(DATASOURCE_KEY_PREFIX + CATEGORIES_KEY_NAME), ",");
        if (arr == null || arr.length == 0) {
            arr = getStringArray(getValue(DATASOURCE_KEY_PREFIX + CATEGORIES_KEY_NAME), ";");
        }
        if (arr != null) {
            set.addAll(Set.of(arr));
        }
        set.add("");
        return set;
    }

    private void setConfiguration(DataSourceConfiguration configuration, Environment environment,
                                  String profile, String category, String doCategory) {
        configuration.setProfile(profile);
        configuration.setCategory(category);
        setConfiguration(configuration, environment, doCategory);
        Map<String, DataSourceConfiguration> map;
        if (configurationMap.containsKey(profile)) {
            map = configurationMap.get(profile);
        } else {
            map = new HashMap<>();
        }
        map.put(category, configuration);
        configurationMap.put(profile, map);
    }

    private DataSourceConfiguration getConfiguration(String profile, String category) {
        Map<String, DataSourceConfiguration> map;
        if (configurationMap.containsKey(profile)) {
            map = configurationMap.get(profile);
        } else {
            map = new HashMap<>();
        }
        DataSourceConfiguration configuration;
        if (map.containsKey(category)) {
            configuration = map.get(category);
        } else {
            configuration = new DataSourceConfiguration();
            configuration.setProfile(profile);
            configuration.setCategory(category);
            map.put(category, configuration);
        }
        configurationMap.put(profile, map);
        return configuration;
    }

    private void setConfiguration(DataSourceConfiguration configuration, Environment environment, String category) {
        String key = DATASOURCE_KEY_PREFIX + category + URL;
        String url = environment.getStringValue(key, "jdbc:mysql://localhost:3306");
        configuration.setUrl(url);

        key = DATASOURCE_KEY_PREFIX + category + DRIVER_NAME;
        String driverName = environment.getValue(key);
        DataSourceDriverName dataSourceDriverName = getDriverName(driverName, url);
        configuration.setDriverName(dataSourceDriverName);

        key = DATASOURCE_KEY_PREFIX + category + USER;
        String user = environment.getStringValue(key, "root");
        configuration.setUser(user);

        key = DATASOURCE_KEY_PREFIX + category + PASSWORD;
        String password = environment.getStringValue(key, null);
        configuration.setPassword(password);

        key = DATASOURCE_KEY_PREFIX + category + AUTO_COMMIT;
        Boolean autoCommit = environment.getBooleanValue(key, false);
        configuration.setAutoCommit(autoCommit);

        key = DATASOURCE_KEY_PREFIX + category + LEVEL;
        String stringValue = environment.getStringValue(key, TransactionIsolationLevel.TRANSACTION_READ_COMMITTED.name().toUpperCase());
        TransactionIsolationLevel transactionIsolationLevel = TransactionIsolationLevel.valueOf(stringValue);
        configuration.setDefaultTransactionIsolationLevel(transactionIsolationLevel);

        String datasourceKey = DATASOURCE_KEY_PREFIX + category + DRIVER;
        Map<String, Object> driverProperties = new HashMap<>(environment.getMatchedKey(key));
        Map<String, Object> properties = new HashMap<>();
        driverProperties.forEach((k, value) -> {
            properties.put(k.substring(datasourceKey.length()), value);
        });
        configuration.setDriverProperties(properties);

        key = DATASOURCE_KEY_PREFIX + category + FACTORY;
        var defaultClassName = "com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory";
        @SuppressWarnings("unchecked")
        Class<? extends DataSourceFactory> dataSourceFactoryClass =
                (Class<? extends DataSourceFactory>) environment.getClassValue(key, defaultClassName);
        configuration.setDataSourceFactoryClass(dataSourceFactoryClass);
    }

    private DataSourceDriverName getDriverName(String driverName, String url) {
        if (StringUtils.hasText(driverName)) {
            DataSourceDriverName custom = DataSourceDriverName.of(driverName);
            custom.setDriverName(driverName);
            return custom;
        } else if (url == null) {
            return null;
        } if (url.startsWith("jdbc:mysql")) {
            return DataSourceDriverName.mysql8;
        } else if (url.startsWith("jdbc:oracle")) {
            return DataSourceDriverName.oracle;
        } else if (url.startsWith("jdbc:db2")) {
            return DataSourceDriverName.db2;
        } else if (url.startsWith("jdbc:sybase:Tds")) {
            return DataSourceDriverName.sybase2;
        } else if (url.startsWith("jdbc:sybase")) {
            return DataSourceDriverName.sybase;
        } else if (url.startsWith("jdbc:mckoi")) {
            return DataSourceDriverName.mckoi;
        } else if (url.startsWith("jdbc:h2")) {
            return DataSourceDriverName.h2;
        } else if (url.startsWith("jdbc:ingres")) {
            return DataSourceDriverName.ingres;
        } else if (url.startsWith("jdbc:sqlite")) {
            return DataSourceDriverName.sqlite;
        } else if (url.startsWith("jdbc:hsqldb")) {
            return DataSourceDriverName.hsqldb;
        } else if (url.startsWith("jdbc:postgresql")) {
            return DataSourceDriverName.postgresql;
        } else if (url.startsWith("jdbc:jtds")) {
            return DataSourceDriverName.jtds;
        } else if (url.startsWith("jdbc:microsoft")) {
            return DataSourceDriverName.sqlserver;
        } else if (url.startsWith("jdbc:log4jdbc")) {
            return DataSourceDriverName.log4jdbc;
        } else if (url.startsWith("jdbc:derby")) {
            return DataSourceDriverName.derby;
        } else if (url.startsWith("jdbc:mariadb")) {
            return DataSourceDriverName.mariadb;
        } else if (url.startsWith("jdbc:cloudscape")) {
            return DataSourceDriverName.cloudscape;
        } else if (url.startsWith("jdbc:informix-sqli")) {
            return DataSourceDriverName.informix;
        } else if (url.startsWith("jdbc:timesten")) {
            return DataSourceDriverName.timesten;
        } else if (url.startsWith("jdbc:as400")) {
            return DataSourceDriverName.as400;
        } else if (url.startsWith("jdbc:sapdb")) {
            return DataSourceDriverName.sapdb;
        } else if (url.startsWith("jdbc:JSQLConnect")) {
            return DataSourceDriverName.jsql;
        } else if (url.startsWith("jdbc:JTurbo")) {
            return DataSourceDriverName.jturbo;
        } else if (url.startsWith("jdbc:firebirdsql")) {
            return DataSourceDriverName.firebirdsql;
        } else if (url.startsWith("jdbc:interbase")) {
            return DataSourceDriverName.interbase;
        } else if (url.startsWith("jdbc:pointbase")) {
            return DataSourceDriverName.pointbase;
        } else if (url.startsWith("jdbc:edbc")) {
            return DataSourceDriverName.edbc;
        } else if (url.startsWith("jdbc:mimer:multi1")) {
            return DataSourceDriverName.mimer;
        }
        return null;
    }

    @Override
    public Map<String, Map<String, DataSourceConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return configurationMap;
    }

    @Override
    public Set<String> getCategories(String profile) {
        return configurationMap.get(profile).keySet();
    }

    @Override
    public DataSourceConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configurationMap.get(profile).get(category);
    }

    public TransactionIsolationLevel getTransactionIsolationLevelValue(String key, TransactionIsolationLevel defaultValue) {
        String stringValue = getStringValue(key, defaultValue.name().toUpperCase());
        return TransactionIsolationLevel.valueOf(stringValue);
    }

    public DataSourceConfiguration getDefaultConfiguration() {
        return configurationMap.get(getDefaultProfile()).get(DEFAULT_CATEGORY);
    }

    public Map<String, DataSourceConfiguration> getConfigurationMap() {
        return new HashMap<>(configurationMap.get(getDefaultProfile()));
    }

    public static DataSourceConfiguration toConfiguration(ApplicationContext applicationContext) {
        return new DataSourceProperties(applicationContext).configurationMap.get(DEFAULT_PROFILE).get(DEFAULT_CATEGORY);
    }

    public DataSourceConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (StringUtils.hasText(name)) {
            return configurationMap.get(getDefaultProfile()).get(name);
        } else {
            return configurationMap.get(getDefaultProfile()).get(DEFAULT_CATEGORY);
        }
    }

    @Override
    public DataSourceConfiguration getConfiguration(ApplicationContext applicationContext) {
        return configurationMap.get(getDefaultProfile()).get(DEFAULT_CATEGORY);
    }

    @Override
    public void close() throws IOException {
        configurationMap.forEach((profile, map) -> map.clear());
        configurationMap.clear();
    }
}
