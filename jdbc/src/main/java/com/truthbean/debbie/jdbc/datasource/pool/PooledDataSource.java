package com.truthbean.debbie.jdbc.datasource.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.ref.Cleaner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This is a simple, synchronous, thread-safe database connection pool.
 *
 * @author Clinton Begin
 */
public class PooledDataSource implements DataSource, AutoCloseable {

    private final DefaultConnectionPool connectionPool;

    private final DefaultDataSourcePoolConfiguration configuration;

    private static final Cleaner cleaner = Cleaner.create();
    private final Cleaner.Cleanable cleanable;

    public PooledDataSource(DefaultDataSourcePoolConfiguration configuration) {
        this.configuration = configuration;
        this.connectionPool = new DefaultConnectionPool(configuration);
        this.cleanable = cleaner.register(this, connectionPool::forceCloseAll);
    }

    public DefaultDataSourcePoolConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.popConnection(configuration.getUser(), configuration.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return connectionPool.popConnection(username, password).getProxyConnection();
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); // requires JDK version 1.6
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}