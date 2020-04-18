package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;

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

    DataSourceDriverName getDriverName();

    Logger getLogger();

    default TransactionInfo getTransaction() {
        try {
            DataSourceDriverName driverName = getDriverName();
            TransactionInfo transactionInfo = new TransactionInfo();
            DriverConnection connection = new DriverConnection();
            connection.setConnection(getDataSource().getConnection());
            connection.setDriverName(driverName);
            transactionInfo.setConnection(connection);
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
