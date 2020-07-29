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

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolConfiguration;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfigurationFactory {

    @SuppressWarnings({"unchecked"})
    public static <Configuration extends DataSourceConfiguration> Configuration factory(
            DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext, Class<Configuration> configurationClass) {
        Set<Configuration> configurations = configurationFactory.getConfigurations(configurationClass, applicationContext);
        for (DataSourceConfiguration configuration : configurations) {
            LOGGER.debug("DataSourcePoolProperties : " + configurationClass);
            if (configurationClass != DefaultDataSourcePoolConfiguration.class && configurationClass != DataSourceConfiguration.class) {
                return (Configuration) configuration;
            }
        }
        return (Configuration) new DefaultDataSourcePoolProperties().toConfiguration(applicationContext);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigurationFactory.class);
}
