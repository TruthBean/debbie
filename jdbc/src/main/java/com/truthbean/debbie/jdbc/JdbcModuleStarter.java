/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.debbie.jdbc.annotation.SqlRepositoryBeanRegister;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactoryFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolConfiguration;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.entity.EntityResolverAware;
import com.truthbean.debbie.jdbc.entity.ResultMapRegister;
import com.truthbean.debbie.jdbc.repository.DdlRepositoryFactory;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevelTransformer;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.transformer.DataTransformerCenter;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class JdbcModuleStarter implements DebbieModuleStarter {

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        beanInfoManager.addIgnoreInterface(EntityResolverAware.class);
        beanInfoManager.addIgnoreInterface(ResultMapRegister.class);
        beanInfoManager.addIgnoreInterface(CreatePost.class);
        beanInfoManager.addIgnoreInterface(ConstructPost.class);

        DataTransformerCenter.register(new TransactionIsolationLevelTransformer(), TransactionIsolationLevel.class, String.class);

        DdlRepositoryFactory factory = new DdlRepositoryFactory("ddlRepository");
        beanInfoManager.registerBeanInfo(factory);
        beanInfoManager.registerBeanAnnotation(SqlRepository.class, new DefaultBeanComponentParser());
        beanInfoManager.registerBeanRegister(new SqlRepositoryBeanRegister());

        DataSourceProperties instance = new DataSourceProperties(applicationContext);
        var dataSourceConfigurationBeanFactory = new PropertiesConfigurationBeanFactory<>(instance, DataSourceConfiguration.class);
        beanInfoManager.registerBeanInfo(dataSourceConfigurationBeanFactory);

        var dataSourcePoolConfigurationBeanFactory = new PropertiesConfigurationBeanFactory<>(new DefaultDataSourcePoolProperties(applicationContext), DefaultDataSourcePoolConfiguration.class);
        beanInfoManager.registerBeanInfo(dataSourcePoolConfigurationBeanFactory);

        registerDataSourceFactory(applicationContext);

        EntityResolver entityResolver = EntityResolver.getInstance();
        BeanLifecycle beanLifecycle = new EntityResolverBeanLifecycle(entityResolver);
        beanInfoManager.registerBeanLifecycle(beanLifecycle);
    }

    // @SuppressWarnings("unchecked")
    private void registerDataSourceFactory(ApplicationContext applicationContext) {
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        BeanInfo<DataSourceFactory> dataSourceFactoryBeanInfo = beanInfoManager.getBeanFactory(null, DataSourceFactory.class, false);
        // Class<? extends DataSourceConfiguration> configurationClass = DataSourceConfiguration.class;
        /*try {
            configurationClass = (Class<? extends DataSourceConfiguration>) Class.forName("com.truthbean.debbie.hikari.HikariConfiguration");
        } catch (ClassNotFoundException e) {
            LOGGER.info("com.truthbean.debbie:debbie-hikari jar not be depended. ");
        }*/
        if (dataSourceFactoryBeanInfo == null) {
            /*Set<DataSourceFactory> factories = DataSourceFactory.factory(applicationContext, DataSourceConfiguration.class);
            for (DataSourceFactory dataSourceFactory : factories) {
                String name = dataSourceFactory.getName();
                SimpleBeanFactory<DataSourceFactory, DataSourceFactory> simpleBeanFactory;
                if ("defaultDataSourceFactory".equals(name)) {
                    simpleBeanFactory = new SimpleBeanFactory<>(dataSourceFactory, DataSourceFactory.class, BeanProxyType.NO, name, "dataSourceFactory", DataSourceFactory.class.getName());
                } else {
                    simpleBeanFactory = new SimpleBeanFactory<>(dataSourceFactory, DataSourceFactory.class, BeanProxyType.NO, name, DataSourceFactory.class.getName());
                }
                beanInfoManager.registerBeanInfo(simpleBeanFactory);
            }*/
            beanInfoManager.registerBeanInfo(new DataSourceFactoryFactory());
        } else {
            if (dataSourceFactoryBeanInfo instanceof MutableBeanFactory<DataSourceFactory> mutableBeanFactory) {
                if (!mutableBeanFactory.isCreated()) {
                    Set<DataSourceFactory> factories = DataSourceFactory.factory(applicationContext, DataSourceConfiguration.class);
                    for (DataSourceFactory dataSourceFactory : factories) {
                        mutableBeanFactory.setBean(dataSourceFactory);
                        // dataSourceFactoryBeanInfo.addBeanName("dataSourceFactory");
                        mutableBeanFactory.addBeanName(dataSourceFactory.getName());
                        mutableBeanFactory.setBeanType(BeanType.SINGLETON);

                        beanInfoManager.registerBeanInfo(mutableBeanFactory);
                        beanInfoManager.refresh(mutableBeanFactory);
                    }
                }
            }
        }
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        TransactionManager.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcModuleStarter.class);
}
