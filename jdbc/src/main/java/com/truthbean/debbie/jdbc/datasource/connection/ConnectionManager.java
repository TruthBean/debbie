package com.truthbean.debbie.jdbc.datasource.connection;

import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ConnectionManager implements Closeable {
    /**
     * 实例化一个线程
     */
    private static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    private boolean autoCommit;
    private TransactionIsolationLevel transactionIsolationLevel = TransactionIsolationLevel.READ_COMMITTED;

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setTransactionIsolationLevel(TransactionIsolationLevel transactionIsolationLevel) {
        if (transactionIsolationLevel != null) {
            this.transactionIsolationLevel = transactionIsolationLevel;
        }
    }

    public void bind(Connection connection) {
        try {
            if (transactionIsolationLevel != TransactionIsolationLevel.NONE) {
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
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void rollback() {
        var connection = connectionThreadLocal.get();
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
