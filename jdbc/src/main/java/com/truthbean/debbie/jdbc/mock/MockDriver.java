package com.truthbean.debbie.jdbc.mock;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockDriver implements Driver {
    public static final String PREFIX = "jdbc:debbie:";

    static {
        try {
            DriverManager.registerDriver(new MockDriver(), new MockDriverAction());
        } catch (SQLException ignored) {
            // ignored
        }
    }

    private MockDriver() {
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        return new MockConnection();
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith(PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 5;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return LOGGER;
    }

    private static final Logger LOGGER = Logger.getLogger(MockDriver.class.getName());
}
