/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.multi.DefaultMultiDataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolFactory;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
     * @return DataSourceFactory
     */
    @SuppressWarnings("unchecked")
    static <Configuration extends DataSourceConfiguration> Set<DataSourceFactory> factory(
            ApplicationContext applicationContext, Class<Configuration> configurationClass) {

        BeanFactory<Configuration> beanFactory =
                applicationContext.getBeanInfoManager().getBeanFactory(null, configurationClass, false);
        if (beanFactory == null) {
            return null;
        }
        Set<DataSourceFactory> factories = new HashSet<>();
        if (beanFactory instanceof PropertiesConfigurationBeanFactory<?, ?> propertiesConfigurationBeanFactory) {
            Collection<?> configurations = propertiesConfigurationBeanFactory.factoryBeans(applicationContext);
            for (Object configuration : configurations) {
                Configuration dataSourceConfiguration = (Configuration) configuration;
                if (dataSourceConfiguration.isEnable()) {
                    factories.add(loadFactory(dataSourceConfiguration));
                }
            }
        }
        return factories;
    }

    static <Configuration extends DataSourceConfiguration> DataSourceFactory factoryOne(
            ApplicationContext applicationContext, Class<Configuration> configurationClass) {
        var config = DataSourceConfigurationFactory.factoryOne(applicationContext, configurationClass);
        return loadFactory(config);
    }

    static <Configuration extends DataSourceConfiguration> DataSourceFactory loadFactory(Configuration configuration) {
        Class<? extends DataSourceFactory> factoryClass = configuration.getDataSourceFactoryClass();
        DataSourceFactory factory;
        if (factoryClass == null || DefaultDataSourceFactory.class.equals(factoryClass)) {
            factory = new DefaultDataSourceFactory();
        } else if (DefaultDataSourcePoolFactory.class.equals(factoryClass)) {
            factory = new DefaultDataSourcePoolFactory();
        } else if (DefaultMultiDataSourceFactory.class.equals(factoryClass)) {
            factory = new DefaultMultiDataSourceFactory();
        } else {
            factory = ReflectionHelper.newInstance(factoryClass);
        }
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

    String getName();

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

}
