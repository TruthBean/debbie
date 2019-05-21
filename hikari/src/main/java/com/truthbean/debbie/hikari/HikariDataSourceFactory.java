package com.truthbean.debbie.hikari;

import com.truthbean.debbie.core.properties.ConfigurationTypeNotMatchedException;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 23:33.
 */
public class HikariDataSourceFactory implements DataSourceFactory {
    private HikariDataSource hikariDataSource;

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSource instanceof HikariDataSource) {
            hikariDataSource = (HikariDataSource) dataSource;
        }
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (configuration instanceof HikariConfiguration) {
            HikariConfiguration hikariConfiguration = (HikariConfiguration) configuration;
            HikariConfig config = hikariConfiguration.getHikariConfig();
            if (hikariConfiguration.getDriverClassName() == null) {
                config.setDriverClassName(configuration.getDriverName());
            }
            if (hikariConfiguration.getJdbcUrl() == null) {
                config.setJdbcUrl(configuration.getUrl());
            }
            if (hikariConfiguration.getUsername() == null) {
                config.setUsername(configuration.getUser());
            }
            if (hikariConfiguration.getHikariPassword() == null) {
                config.setPassword(configuration.getPassword());
            }
            if (hikariConfiguration.getHikariAutoCommit() == null) {
                config.setAutoCommit(configuration.getAutoCommit());
            }
            if (hikariConfiguration.getTransactionIsolation() == null) {
                config.setTransactionIsolation(configuration.getDefaultTransactionIsolationLevel().name());
            }
            hikariDataSource = new HikariDataSource(config);
        } else {
            throw new ConfigurationTypeNotMatchedException();
        }

        return this;
    }

    @Override
    public DataSource getDataSource() {
        return hikariDataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
