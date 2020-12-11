/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.MutableBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@SuppressWarnings({"unchecked"})
public class DataSourceFactoryBeanRegister extends SingletonBeanRegister {

    private final ApplicationContext applicationContext;
    private final BeanInitialization initialization;
    private final DebbieConfigurationCenter configurationFactory;

    private final Logger logger = LoggerFactory.getLogger(DataSourceFactoryBeanRegister.class);

    public DataSourceFactoryBeanRegister(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.initialization = applicationContext.getBeanInitialization();
        this.configurationFactory = configurationFactory;
    }

    public void registerDataSourceFactory() {
        MutableBeanInfo<DataSourceFactory> dataSourceFactoryBeanInfo = initialization.getRegisterRawBean(DataSourceFactory.class);
        Class<? extends DataSourceConfiguration> configurationClass = DataSourceConfiguration.class;
        try {
            configurationClass = (Class<? extends DataSourceConfiguration>) Class.forName("com.truthbean.debbie.hikari.HikariConfiguration");
        } catch (ClassNotFoundException e) {
            logger.info("com.truthbean.debbie:debbie-hikari jar not be depended. ");
        }
        if (dataSourceFactoryBeanInfo == null) {
            DataSourceFactory dataSourceFactory = DataSourceFactory.factory(configurationFactory, applicationContext, configurationClass);
            registerSingletonBean(dataSourceFactory, DataSourceFactory.class, "dataSourceFactory");
        } else if (dataSourceFactoryBeanInfo.isEmpty()) {
            DataSourceFactory dataSourceFactory = DataSourceFactory.factory(configurationFactory, applicationContext, configurationClass);
            dataSourceFactoryBeanInfo.setBean(dataSourceFactory);
            dataSourceFactoryBeanInfo.addBeanName("dataSourceFactory");
            registerSingletonBean(dataSourceFactoryBeanInfo);
        }
    }
}
