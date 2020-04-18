package com.truthbean.debbie.jdbc;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.annotation.SqlRepositoryRegister;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactoryBeanRegister;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;
import com.truthbean.debbie.jdbc.repository.DdlRepository;
import com.truthbean.debbie.jdbc.repository.DdlRepositoryFactory;
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
    public void registerBean(BeanFactoryHandler beanFactoryHandler, BeanInitialization beanInitialization) {
        beanInitialization.addAnnotationRegister(new SqlRepositoryRegister(beanInitialization));

        beanInitialization.registerDataTransformer(new TransactionIsolationLevelTransformer(), TransactionIsolationLevel.class, String.class);

        MethodProxyHandlerRegister methodProxyHandlerRegister = beanFactoryHandler.getMethodProxyHandlerRegister();
        methodProxyHandlerRegister.register(JdbcTransactional.class, TransactionalMethodProxyHandler.class);

        DebbieBeanInfo<DdlRepository> beanInfo = new DebbieBeanInfo<>(DdlRepository.class);
        beanInfo.setBeanName("ddlRepository");
        DdlRepositoryFactory factory = new DdlRepositoryFactory();
        factory.setBeanFactoryHandler(beanFactoryHandler);
        beanInfo.setBeanFactory(factory);
        beanInitialization.initBean(beanInfo);
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(DataSourceProperties.class, DataSourceConfiguration.class);
        configurationFactory.register(DefaultDataSourcePoolProperties.class, DataSourceConfiguration.class);

        var register = new DataSourceFactoryBeanRegister(configurationFactory, beanFactoryHandler);
        register.registerDataSourceFactory();

        beanFactoryHandler.refreshBeans();
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void release(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        TransactionManager.clear();
    }
}
