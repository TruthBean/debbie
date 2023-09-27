/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolConfiguration;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfigurationFactory {

    @SuppressWarnings({"unchecked"})
    public static <Configuration extends DataSourceConfiguration> Collection<Configuration> factory(
            ApplicationContext applicationContext, Class<Configuration> configurationClass) {
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        Set<Configuration> configurations = globalBeanFactory.getBeanList(configurationClass);
        if (!configurations.isEmpty()) {
            return configurations;
        } else {
            var configuration = (Configuration) new DefaultDataSourcePoolProperties().getConfiguration(applicationContext);
            Collection<Configuration> collection = new HashSet<>();
            collection.add(configuration);
            return collection;
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <Configuration extends DataSourceConfiguration> Configuration factoryOne(
            ApplicationContext applicationContext, Class<Configuration> configurationClass) {
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        Set<Configuration> configurations = globalBeanFactory.getBeanList(configurationClass);
        for (Configuration configuration : configurations) {
            LOGGER.debug("DataSourcePoolProperties : " + configurationClass);
            if (configurationClass != DefaultDataSourcePoolConfiguration.class && configurationClass != DataSourceConfiguration.class) {
                return configuration;
            }
        }
        return  (Configuration) new DefaultDataSourcePoolProperties().getConfiguration(applicationContext);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigurationFactory.class);
}
