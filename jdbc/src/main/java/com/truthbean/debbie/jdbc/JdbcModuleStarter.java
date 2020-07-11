/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactoryBeanRegister;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;
import com.truthbean.debbie.jdbc.repository.CustomRepositoryFactory;
import com.truthbean.debbie.jdbc.repository.DdlRepository;
import com.truthbean.debbie.jdbc.repository.DdlRepositoryFactory;
import com.truthbean.debbie.jdbc.repository.JdbcRepository;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevelTransformer;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.jdbc.transaction.TransactionalMethodProxyHandler;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.proxy.MethodProxyHandlerRegister;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class JdbcModuleStarter implements DebbieModuleStarter {

    @Override
    public void registerBean(DebbieApplicationContext applicationContext, BeanInitialization beanInitialization) {
        beanInitialization.registerDataTransformer(new TransactionIsolationLevelTransformer(), TransactionIsolationLevel.class, String.class);

        MethodProxyHandlerRegister methodProxyHandlerRegister = applicationContext.getMethodProxyHandlerRegister();
        methodProxyHandlerRegister.register(JdbcTransactional.class, TransactionalMethodProxyHandler.class);

        registerDdlRepository(applicationContext, beanInitialization);
        registerCustomRepository(beanInitialization, applicationContext.getGlobalBeanFactory());
    }

    private void registerDdlRepository(DebbieApplicationContext applicationContext, BeanInitialization beanInitialization) {
        DebbieBeanInfo<DdlRepository> beanInfo = new DebbieBeanInfo<>(DdlRepository.class);
        beanInfo.addBeanName("ddlRepository");
        DdlRepositoryFactory factory = new DdlRepositoryFactory();
        factory.setGlobalBeanFactory(applicationContext.getGlobalBeanFactory());
        beanInfo.setBeanFactory(factory);
        beanInitialization.initBean(beanInfo);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerCustomRepository(BeanInitialization beanInitialization, GlobalBeanFactory beanFactory) {
        Set<DebbieBeanInfo<?>> repository = beanInitialization.getAnnotatedClass(SqlRepository.class);
        for (DebbieBeanInfo<?> debbieBeanInfo : repository) {
            if (debbieBeanInfo.isAssignable(JdbcRepository.class)) {
                BeanFactory repositoryFactory = new CustomRepositoryFactory(debbieBeanInfo);
                repositoryFactory.setGlobalBeanFactory(beanFactory);
                debbieBeanInfo.setBeanFactory(repositoryFactory);
            }
        }
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, DebbieApplicationContext applicationContext) {
        configurationFactory.register(DataSourceProperties.class, DataSourceConfiguration.class);
        configurationFactory.register(DefaultDataSourcePoolProperties.class, DataSourceConfiguration.class);

        var register = new DataSourceFactoryBeanRegister(configurationFactory, applicationContext);
        register.registerDataSourceFactory();

        applicationContext.refreshBeans();
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, DebbieApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void release(DebbieConfigurationFactory configurationFactory, DebbieApplicationContext applicationContext) {
        TransactionManager.clear();
    }
}
