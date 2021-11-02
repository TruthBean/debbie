/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.common.mini.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:49.
 */
public class DataSourceProperties extends EnvironmentContentHolder implements DebbieProperties<DataSourceConfiguration>, Closeable {
    private final DataSourceConfiguration defaultConfiguration;
    private final Map<String, DataSourceConfiguration> configurationMap = new HashMap<>();

    protected static final String DATASOURCE_KEY_PREFIX = "debbie.datasource.";

    private static final String DRIVER_NAME = ".driver-name";
    private static final String URL = ".url";
    private static final String USER = ".user";
    private static final String PASSWORD = ".password";

    private static final String AUTO_COMMIT = ".auto-commit";
    private static final String LEVEL = ".default-transaction-isolation-level";
    private static final String DRIVER = ".driver.";
    private static final String FACTORY = ".factory";
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
    private static final DataSourceProperties INSTANCE = new DataSourceProperties();

    public DataSourceProperties() {
        defaultConfiguration = new DataSourceConfiguration();
        setDefaultConfiguration();
        setCustomConfiguration();
    }

    private void setCustomConfiguration() {
        Map<String, String> dataSourceProperties = getMatchedKey(DATASOURCE_KEY_PREFIX);
        dataSourceProperties.forEach((k, v) -> {
            if (!k.equals(URL_KEY) && k.endsWith(URL)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.length() - URL.length();
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                String url = getText(v, "jdbc:mysql://localhost:3306");
                configuration.setUrl(url);
            }
            if (!k.equals(DRIVER_NAME_KEY) && k.endsWith(DRIVER_NAME)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.length() - DRIVER_NAME.length();
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                configuration.setDriverName(getDriverName(v, configuration.getUrl()));
            }
            if (!k.equals(USER_KEY) && k.endsWith(USER)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.length() - USER.length();
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                String user = getText(v, "root");
                configuration.setUser(user);
            }
            if (!k.equals(PASSWORD_KEY) && k.endsWith(PASSWORD)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.length() - PASSWORD.length();
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                configuration.setPassword(v);
            }
            if (!k.equals(AUTO_COMMIT_KEY) && k.endsWith(AUTO_COMMIT)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.length() - AUTO_COMMIT.length();
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                configuration.setAutoCommit(getBoolean(v, false));
            }
            if (!k.equals(DEFAULT_TRANSACTION_ISOLATION_LEVEL) && k.endsWith(LEVEL)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.length() - LEVEL.length();
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                if (StringUtils.hasText(v)) {
                    TransactionIsolationLevel transactionIsolationLevel = TransactionIsolationLevel.valueOf(v.toUpperCase());
                    configuration.setDefaultTransactionIsolationLevel(transactionIsolationLevel);
                } else {
                    configuration.setDefaultTransactionIsolationLevel(TransactionIsolationLevel.TRANSACTION_READ_COMMITTED);
                }
            }
            if (!k.equals(DATA_SOURCE_FACTORY) && k.endsWith(FACTORY)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.length() - FACTORY.length();
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                var defaultClassName = "com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory";
                @SuppressWarnings("unchecked")
                Class<? extends DataSourceFactory> dataSourceFactoryClass = (Class<? extends DataSourceFactory>) getClass(v, defaultClassName);
                configuration.setDataSourceFactoryClass(dataSourceFactoryClass);
            }
            if (!k.startsWith(DRIVER_PROPERTIES_PREFIX) && k.startsWith(DATASOURCE_KEY_PREFIX) && k.contains(DRIVER)) {
                var startIndex = DATASOURCE_KEY_PREFIX.length();
                var endIndex = k.indexOf(DRIVER);
                String name = k.substring(startIndex, endIndex);
                DataSourceConfiguration configuration = getConfiguration(name);
                Map<String, Object> driverProperties;
                if (configuration.getDriverProperties() != null) {
                    driverProperties = configuration.getDriverProperties();
                } else {
                    driverProperties = new HashMap<>();
                }
                String key = k.substring(k.indexOf(DRIVER) + DRIVER.length());
                driverProperties.put(key, v);
                configuration.setDriverProperties(driverProperties);
            }
        });
    }

    private DataSourceConfiguration getConfiguration(String name) {
        DataSourceConfiguration configuration;
        if (configurationMap.containsKey(name)) {
            configuration = configurationMap.get(name);
        } else {
            configuration = new DataSourceConfiguration();
            configuration.setName(name);
            configurationMap.put(name, configuration);
        }
        return configuration;
    }

    private void setDefaultConfiguration() {
        defaultConfiguration.setName("default");
        String url = getStringValue(URL_KEY, "jdbc:mysql://localhost:3306");
        defaultConfiguration.setUrl(url);

        String driverName = getValue(DRIVER_NAME_KEY);
        DataSourceDriverName dataSourceDriverName = getDriverName(driverName, url);
        defaultConfiguration.setDriverName(dataSourceDriverName);

        String user = getStringValue(USER_KEY, "root");
        defaultConfiguration.setUser(user);

        String password = getStringValue(PASSWORD_KEY, null);
        defaultConfiguration.setPassword(password);

        Boolean autoCommit = getBooleanValue(AUTO_COMMIT_KEY, false);
        defaultConfiguration.setAutoCommit(autoCommit);

        TransactionIsolationLevel transactionIsolationLevel =
                getTransactionIsolationLevelValue(DEFAULT_TRANSACTION_ISOLATION_LEVEL, TransactionIsolationLevel.TRANSACTION_READ_COMMITTED);
        defaultConfiguration.setDefaultTransactionIsolationLevel(transactionIsolationLevel);

        Map<String, Object> driverProperties = new HashMap<>(getMatchedKey(DRIVER_PROPERTIES_PREFIX));
        Map<String, Object> properties = new HashMap<>();
        driverProperties.forEach((key, value) -> {
            String k = key.substring(DRIVER_PROPERTIES_PREFIX.length());
            properties.put(k, value);
        });
        defaultConfiguration.setDriverProperties(properties);

        var defaultClassName = "com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory";
        @SuppressWarnings("unchecked")
        Class<? extends DataSourceFactory> dataSourceFactoryClass =
                (Class<? extends DataSourceFactory>) getClassValue(DATA_SOURCE_FACTORY, defaultClassName);
        defaultConfiguration.setDataSourceFactoryClass(dataSourceFactoryClass);
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

    public TransactionIsolationLevel getTransactionIsolationLevelValue(String key, TransactionIsolationLevel defaultValue) {
        String stringValue = getStringValue(key, defaultValue.name().toUpperCase());
        return TransactionIsolationLevel.valueOf(stringValue);
    }

    public static DataSourceProperties getInstance() {
        return INSTANCE;
    }

    public DataSourceConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public DataSourceConfiguration getConfiguration() {
        return defaultConfiguration;
    }

    public Map<String, DataSourceConfiguration> getConfigurationMap() {
        return new HashMap<>(configurationMap);
    }

    public static DataSourceConfiguration toConfiguration() {
        return INSTANCE.defaultConfiguration;
    }

    @Override
    public DataSourceConfiguration toConfiguration(ApplicationContext applicationContext) {
        return defaultConfiguration;
    }

    @Override
    public void close() throws IOException {
        configurationMap.clear();
    }
}
