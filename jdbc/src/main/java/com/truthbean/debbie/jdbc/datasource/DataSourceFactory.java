/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DataSourceFactory extends BeanClosure {

    /**
     * create DataSourceFactory by application.properties
     * @param <Configuration> DataSourceConfiguration subclass
     * @param applicationContext applicationContext
     * @param configurationClass configurationClass
     * @param configurationFactory configurationFactory
     * @return DataSourceFactory
     */
    static <Configuration extends DataSourceConfiguration> DataSourceFactory factory(
            DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext, Class<Configuration> configurationClass) {
        var config = DataSourceConfigurationFactory.factory(configurationFactory, applicationContext, configurationClass);
        return loadFactory(config);
    }

    static <Configuration extends DataSourceConfiguration> DataSourceFactory loadFactory(Configuration configuration) {
        DataSourceFactory factory = ReflectionHelper.newInstance(configuration.getDataSourceFactoryClass());
        return factory.factory(configuration);
    }

    /**
     * create DataSourceFactory by DataSource
     *
     * @param dataSource dataSource
     * @return DataSourceFactory
     */
    DataSourceFactory factory(DataSource dataSource);

    /**
     * create DataSourceFactory by DataSourceConfiguration
     *
     * @param configuration configuration
     * @return DataSourceFactory
     */
    DataSourceFactory factory(DataSourceConfiguration configuration);

    /**
     * get DataSource
     *
     * @return DataSource
     */
    DataSource getDataSource();

    DataSourceDriverName getDriverName();

    Logger getLogger();

    default TransactionInfo getTransaction() {
        try {
            DataSourceDriverName driverName = getDriverName();
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setConnection(getDataSource().getConnection());
            transactionInfo.setDriverName(driverName);
            return transactionInfo;
        } catch (SQLException e) {
            getLogger().error("", e);
        }
        return null;
    }

    default Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            getLogger().error("", e);
        }
        return null;
    }

    /**
     * close dataSource
     * @since 0.0.2
     */
    @Override
    default void destroy() {
        // do nothing
    }

}
