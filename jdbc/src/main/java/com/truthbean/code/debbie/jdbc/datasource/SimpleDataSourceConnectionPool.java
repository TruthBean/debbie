package com.truthbean.code.debbie.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-26 16:25
 */
public class SimpleDataSourceConnectionPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataSourceConnectionPool.class);

    private Queue<Connection> connectionsPool = new ConcurrentLinkedQueue<>();
    private final DataSourcePoolConfiguration configuration;

    public SimpleDataSourceConnectionPool(DataSourcePoolConfiguration configuration) throws Exception {
        this.configuration = configuration.clone();
        Class.forName(configuration.getDriverName());

        var driver = DriverManager.getDriver(configuration.getUrl());
        DriverManager.registerDriver(driver);

        for (int i = 0; i < configuration.getInitialPoolSize(); i++) {
            connectionsPool.add(createConnection());
        }
    }

    public Connection getConnection() {
        int size = connectionsPool.size();
        Connection connection = null;
        if (size > 0) {
            connection = connectionsPool.poll();
        }
        if (size <= configuration.getInitialPoolSize()) {
            increaseConnection();
        }
        return connection;
    }

    private void increaseConnection() {
        int size = connectionsPool.size();
        var maxSize = configuration.getMaxPoolSize();
        var increase = configuration.getIncrease();
        if (size < maxSize) {
            int in;
            if (maxSize - size >= increase) {
                in = increase;
            } else {
                in = maxSize - size;
            }
            for (int i = 0; i < in; i++) {
                connectionsPool.add(createConnection());
            }
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(configuration.getUrl(), configuration.getUser(),
                    configuration.getPassword());
        } catch (SQLException e) {
            LOGGER.error("", e);
            throw new RuntimeException(e);
        }
    }
}
