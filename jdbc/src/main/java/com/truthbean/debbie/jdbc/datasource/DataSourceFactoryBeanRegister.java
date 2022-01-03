/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

*/
/**
 * @author TruthBean
 * @since 0.0.2
 *//*

@SuppressWarnings({"unchecked"})
public class DataSourceFactoryBeanRegister extends SingletonBeanRegister {

    private final ApplicationContext applicationContext;
    private final BeanInfoManager beanInfoManager;
    private final DebbieConfigurationCenter configurationFactory;

    private final Logger logger = LoggerFactory.getLogger(DataSourceFactoryBeanRegister.class);

    public DataSourceFactoryBeanRegister(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.beanInfoManager = applicationContext.getBeanInfoManager();
        this.configurationFactory = configurationFactory;
    }

    public void registerDataSourceFactory() {
        BeanInfo<DataSourceFactory> dataSourceFactoryBeanInfo = beanInfoManager.getRegisterRawBean(DataSourceFactory.class);
        Class<? extends DataSourceConfiguration> configurationClass = DataSourceConfiguration.class;
        try {
            configurationClass = (Class<? extends DataSourceConfiguration>) Class.forName("com.truthbean.debbie.hikari.HikariConfiguration");
        } catch (ClassNotFoundException e) {
            logger.info("com.truthbean.debbie:debbie-hikari jar not be depended. ");
        }
        if (dataSourceFactoryBeanInfo == null) {
            Set<DataSourceFactory> factories = DataSourceFactory.factory(configurationFactory, applicationContext, configurationClass);
            for (DataSourceFactory dataSourceFactory : factories) {
                String name = dataSourceFactory.getName();
                if ("defaultDataSourceFactory".equals(name)) {
                    Set<String> names = new HashSet<>();
                    names.add(name);
                    names.add("dataSourceFactory");
                    registerSingletonBean(dataSourceFactory, DataSourceFactory.class, names);
                } else {
                    registerSingletonBean(dataSourceFactory, DataSourceFactory.class, name);
                }
            }
        } else {
            if (dataSourceFactoryBeanInfo instanceof MutableFactoryBeanInfo<DataSourceFactory> mutableBeanInfo) {
                if (mutableBeanInfo.isEmpty()) {
                    Set<DataSourceFactory> factories = DataSourceFactory.factory(configurationFactory, applicationContext, configurationClass);
                    for (DataSourceFactory dataSourceFactory : factories) {
                        mutableBeanInfo.setBean(dataSourceFactory);
                        // dataSourceFactoryBeanInfo.addBeanName("dataSourceFactory");
                        mutableBeanInfo.addBeanName(dataSourceFactory.getName());
                        registerSingletonBean(mutableBeanInfo);
                    }
                }
            } else if (dataSourceFactoryBeanInfo instanceof MutableBeanFactory<DataSourceFactory> mutableBeanFactory) {
                if (!mutableBeanFactory.isCreated()) {
                    Set<DataSourceFactory> factories = DataSourceFactory.factory(configurationFactory, applicationContext, configurationClass);
                    for (DataSourceFactory dataSourceFactory : factories) {
                        mutableBeanFactory.setBean(dataSourceFactory);
                        // dataSourceFactoryBeanInfo.addBeanName("dataSourceFactory");
                        mutableBeanFactory.addBeanName(dataSourceFactory.getName());
                        registerSingletonBean(mutableBeanFactory);

                        mutableBeanFactory.setBeanType(BeanType.SINGLETON);
                        beanInfoManager.refresh(mutableBeanFactory);
                        beanInfoManager.refreshBeans();
                    }
                }
            }
        }
    }
}
*/
