package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.reflection.ClassLoaderUtils;

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

    private DataSourceConfiguration configuration;

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
        if (!REGISTERED_DRIVERS.containsKey(configuration.getDriverName())) {
            Class<?> driverType;
            ClassLoader driverClassLoader = ClassLoaderUtils.getClassLoader(DataSource.class);
            try {
                var driver = configuration.getDriverName();
                if (driverClassLoader != null) {
                    driverType = Class.forName(configuration.getDriverName(), true, driverClassLoader);
                } else {
                    driverType = ClassLoader.getSystemClassLoader().loadClass(driver);
                }
                Driver driverInstance = (Driver) driverType.getDeclaredConstructor().newInstance();
                DriverManager.registerDriver(driverInstance);
                REGISTERED_DRIVERS.put(driver, driverInstance);
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    private void configureConnection(Connection connection) throws SQLException {
        var autoCommit = configuration.getAutoCommit();
        if (autoCommit != null && autoCommit != connection.getAutoCommit()) {
            connection.setAutoCommit(autoCommit);
        }
        var defaultTransactionIsolationLevel = configuration.getDefaultTransactionIsolationLevel();
        if (defaultTransactionIsolationLevel != null) {
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
