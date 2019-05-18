package com.truthbean.debbie.jdbc.datasource.connection;

import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ConnectionBinder implements Closeable {

    private static volatile DataSourceFactory dataSourceFactory;

    /**
     * 实例化一个线程
     */
    private static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    private boolean autoCommit = false;
    private TransactionIsolationLevel transactionIsolationLevel = TransactionIsolationLevel.TRANSACTION_READ_COMMITTED;

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setTransactionIsolationLevel(TransactionIsolationLevel transactionIsolationLevel) {
        if (transactionIsolationLevel != null) {
            this.transactionIsolationLevel = transactionIsolationLevel;
        }
    }

    public DataSourceFactory getDataSourceFactory(DataSourceConfiguration configuration) {
        if (dataSourceFactory == null) {
            synchronized (ConnectionBinder.class) {
                if (dataSourceFactory == null) {
                    DataSourceFactory factory = ReflectionHelper.newInstance(configuration.getDataSourceFactoryClass());
                    dataSourceFactory = factory.factory(configuration);
                }
            }

        }
        return dataSourceFactory;
    }

    public void bind(Connection connection) {
        try {
            if (transactionIsolationLevel != TransactionIsolationLevel.TRANSACTION_NONE) {
                connection.setAutoCommit(autoCommit);
                connection.setTransactionIsolation(transactionIsolationLevel.getLevel());
            }
            connectionThreadLocal.set(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从当前运行线程中获取与其绑定的数据连接
     *
     * @return connection
     */
    public static Connection get() {
        return connectionThreadLocal.get();
    }

    @Override
    public void close() {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connectionThreadLocal.remove();
    }

    public void commit() {
        var connection = connectionThreadLocal.get();
        try {
            if (connection != null && !connection.isReadOnly() && !connection.getAutoCommit()) {
                LOGGER.debug("commit ...");
                connection.commit();
            }
        } catch (SQLException e) {
            LOGGER.error("commit error for " + e.getMessage());
        }
    }

    public void rollback() {
        var connection = connectionThreadLocal.get();
        try {
            if (connection != null && !connection.isReadOnly()) {
                LOGGER.debug("rollback ...");
                connection.rollback();
            }
        } catch (SQLException e) {
            LOGGER.error("rollback error for " + e.getMessage());
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionBinder.class);
}
