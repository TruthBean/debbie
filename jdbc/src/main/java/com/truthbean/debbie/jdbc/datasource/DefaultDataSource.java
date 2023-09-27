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

import com.truthbean.debbie.reflection.ClassLoaderUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSource implements DataSource {
    private static final Map<String, Driver> REGISTERED_DRIVERS = new ConcurrentHashMap<>();

    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            REGISTERED_DRIVERS.put(driver.getClass().getName(), driver);
        }
    }

    private final DataSourceConfiguration configuration;

    public DefaultDataSource(DataSourceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(configuration.getUser(), configuration.getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Properties props = new Properties();
        var driverProperties = configuration.getDriverProperties();
        if (driverProperties != null) {
            props.putAll(driverProperties);
        }
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        return getConnection(props);
    }

    public Connection getConnection(Properties properties) throws SQLException {
        initializeDriver();
        Connection connection = DriverManager.getConnection(configuration.getUrl(), properties);
        configureConnection(connection);
        return connection;
    }

    private synchronized void initializeDriver() throws SQLException {
        DataSourceDriverName dataSourceDriverName = configuration.getDriverName();
        if (dataSourceDriverName != null && !REGISTERED_DRIVERS.containsKey(dataSourceDriverName.getDriverName())) {
            Class<?> driverType = null;
            ClassLoader driverClassLoader = ClassLoaderUtils.getClassLoader(DataSource.class);
            try {
                var driverName = dataSourceDriverName.getDriverName();
                try {
                    if (driverClassLoader != null) {
                        driverType = Class.forName(driverName, true, driverClassLoader);
                    } else {
                        driverType = ClassLoader.getSystemClassLoader().loadClass(driverName);
                    }
                } catch (ClassNotFoundException e) {
                    System.getLogger(DefaultDataSource.class.getName())
                            .log(System.Logger.Level.WARNING,
                                    "No driver(" + driverName + ") found, fall to use mysql driver!");
                    try {
                        if (dataSourceDriverName == DataSourceDriverName.mysql8) {
                            dataSourceDriverName = DataSourceDriverName.mysql;
                            driverName = dataSourceDriverName.getDriverName();
                            if (driverClassLoader != null) {
                                driverType = Class.forName(driverName, true, driverClassLoader);
                            } else {
                                driverType = ClassLoader.getSystemClassLoader().loadClass(driverName);
                            }
                        }
                    } catch (ClassNotFoundException classNotFoundException) {
                        throw new SQLException("No driver(" + driverName + ") found found. ");
                    }
                }
                if (driverType != null) {
                    Driver driverInstance = (Driver) driverType.getDeclaredConstructor().newInstance();
                    DriverManager.registerDriver(driverInstance);
                    REGISTERED_DRIVERS.put(driverName, driverInstance);
                } else {
                    throw new SQLException("No driver(" + driverName + ") found found. ");
                }
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    private void configureConnection(final Connection connection) throws SQLException {
        var autoCommit = configuration.getAutoCommit();
        if (autoCommit != null && autoCommit != connection.getAutoCommit()) {
            connection.setAutoCommit(autoCommit);
        }
        var defaultTransactionIsolationLevel = configuration.getDefaultTransactionIsolationLevel();
        var driverName = configuration.getDriverName();
        if (driverName != DataSourceDriverName.sqlite && defaultTransactionIsolationLevel != null) {
            connection.setTransactionIsolation(defaultTransactionIsolationLevel.getLevel());
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
