/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.hikari;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.properties.ConfigurationTypeNotMatchedException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 23:33.
 */
public class HikariDataSourceFactory implements DataSourceFactory {
    private HikariDataSource hikariDataSource;
    private DataSourceDriverName driverName;

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
            DataSourceDriverName driverName = configuration.getDriverName();
            if (hikariConfiguration.getDriverClassName() == null && driverName != null) {
                this.driverName = driverName;
                config.setDriverClassName(driverName.getDriverName());
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
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public void destroy() {
        hikariDataSource.close();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private static final Logger logger = LoggerFactory.getLogger(HikariDataSourceFactory.class);
}
