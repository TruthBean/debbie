package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.transaction.DefaultTransactionProvider;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class DebbieApplicationRepository<Domain, Id> extends DebbieRepository<Domain, Id>
        implements DefaultTransactionProvider, Closeable {

    private volatile Supplier<DataSourceFactory> dataSourceFactorySupplier;

    public DebbieApplicationRepository() {
        super();
    }

    public DebbieApplicationRepository(Class<Id> idClass, Class<Domain> entityClass, EntityResolver entityResolver) {
        super(idClass, entityClass, entityResolver);
    }

    public DebbieApplicationRepository<Domain, Id> setDataSourceFactory(Supplier<DataSourceFactory> dataSourceFactorySupplier) {
        this.dataSourceFactorySupplier = dataSourceFactorySupplier;
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
