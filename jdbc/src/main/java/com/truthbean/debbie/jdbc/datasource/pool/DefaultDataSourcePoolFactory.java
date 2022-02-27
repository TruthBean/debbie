/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.properties.ConfigurationTypeNotMatchedException;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import javax.sql.DataSource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourcePoolFactory implements DataSourceFactory {
    private PooledDataSource pooledDataSource;
    private DataSourceDriverName driverName;
    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSource instanceof PooledDataSource) {
            pooledDataSource = (PooledDataSource) dataSource;
        } else {
            throw new ConfigurationTypeNotMatchedException();
        }
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (configuration instanceof DefaultDataSourcePoolConfiguration) {
            pooledDataSource = new PooledDataSource((DefaultDataSourcePoolConfiguration) configuration);
            driverName = configuration.getDriverName();
        } else {
            throw new ConfigurationTypeNotMatchedException();
        }
        return this;
    }

    @Override
    public String getName() {
        return "defaultDataSourcePoolFactory";
    }

    @Override
    public DataSource getDataSource() {
        return pooledDataSource;
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void close() {
        pooledDataSource.close();
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataSourcePoolFactory.class);
}
