package com.truthbean.code.debbie.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

    private Connection connection;

    public DataSourceFactory(DataSource dataSource) {
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("数据库连接错误！", e);
        }
    }

    public DataSourceFactory(SingleDataSourceConnectionContext connectionContext) {
        connection = connectionContext.get();
    }

    public Connection getConnection() {
        return connection;
    }

}
