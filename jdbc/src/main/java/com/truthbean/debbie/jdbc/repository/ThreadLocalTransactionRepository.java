package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.threadlocal.ThreadLocalDataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.threadlocal.ThreadLocalDataSourceFactory;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class ThreadLocalTransactionRepository<Domain, Id> extends DebbieApplicationRepository<Domain, Id>
        implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ThreadLocalDataSourceConfiguration threadLocalDataSourceConfiguration =
                applicationContext.getGlobalBeanFactory().factoryIfPresent(ThreadLocalDataSourceConfiguration.class);
        if (threadLocalDataSourceConfiguration != null) {
            threadLocalDataSourceConfiguration.setThreadLocal(true);
            Supplier<DataSourceFactory> dataSourceFactorySupplier = () -> new ThreadLocalDataSourceFactory().factory(threadLocalDataSourceConfiguration);
            setDataSourceFactory(dataSourceFactorySupplier);
        }
    }

    private static volatile Supplier<DataSourceFactory> dataSourceFactorySupplier;

    public ThreadLocalTransactionRepository<Domain, Id> setDataSourceFactory(Supplier<DataSourceFactory> dataSourceFactorySupplier) {
        ThreadLocalTransactionRepository.dataSourceFactorySupplier = dataSourceFactorySupplier;
        return this;
    }

    @Override
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactorySupplier.get();
    }

    @Override
    public void close() throws IOException {
        if (dataSourceFactorySupplier != null) {
            dataSourceFactorySupplier.get().close();
        }
        dataSourceFactorySupplier = null;
    }
}
