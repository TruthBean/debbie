package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;

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
     * @param beanFactoryHandler beanFactoryHandler
     * @param configurationClass configurationClass
     * @param configurationFactory configurationFactory
     * @return DataSourceFactory
     */
    static <Configuration extends DataSourceConfiguration> DataSourceFactory factory(
        DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler, Class<Configuration> configurationClass) {
        var config = DataSourceConfigurationFactory.factory(configurationFactory, beanFactoryHandler, configurationClass);
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

    default TransactionInfo getTransaction() {
        try {
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setConnection(getDataSource().getConnection());
            return transactionInfo;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    default Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
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
