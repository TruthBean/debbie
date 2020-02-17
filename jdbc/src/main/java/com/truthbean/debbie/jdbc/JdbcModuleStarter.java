package com.truthbean.debbie.jdbc;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.annotation.SqlRepositoryRegister;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactoryBeanRegister;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevelTransformer;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.jdbc.transaction.TransactionalMethodProxyHandler;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.proxy.MethodProxyHandlerRegister;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class JdbcModuleStarter implements DebbieModuleStarter {

    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler) {
        DebbieConfigurationFactory configurationFactory = beanFactoryHandler.getConfigurationFactory();
        configurationFactory.register(DataSourceProperties.class);
        configurationFactory.register(DefaultDataSourcePoolProperties.class);

        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        beanInitialization.addAnnotationRegister(new SqlRepositoryRegister());

        beanInitialization.registerDataTransformer(new TransactionIsolationLevelTransformer(), TransactionIsolationLevel.class, String.class);

        MethodProxyHandlerRegister methodProxyHandlerRegister = beanFactoryHandler.getMethodProxyHandlerRegister();
        methodProxyHandlerRegister.register(JdbcTransactional.class, TransactionalMethodProxyHandler.class);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        var register = new DataSourceFactoryBeanRegister(configurationFactory, beanFactoryHandler);
        register.registerDataSourceFactory();
        beanFactoryHandler.refreshBeans();
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void release() {
        TransactionManager.clear();
    }
}
