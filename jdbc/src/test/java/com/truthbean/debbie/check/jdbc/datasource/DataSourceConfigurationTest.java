/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.datasource;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanInjection;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.repository.DdlRepository;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Set;

/**
 * @author truthbean/Rogar·Q
 * @since Created on 2020-04-13 13:51.
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
@JdbcTransactional
public class DataSourceConfigurationTest {

    static {
        System.setProperty("logging.level.com.truthbean.debbie", "info");
    }

    private final DataSourceConfiguration configuration;
    private final DataSourceConfiguration mariadbConfiguration;
    private final DataSourceConfiguration h2Configuration;

    private final DdlRepository ddlRepository;

    public DataSourceConfigurationTest(@BeanInject(name = "dataSourceConfiguration") DataSourceConfiguration configuration,
                                       @BeanInject(profile = "mariadb") DataSourceConfiguration mariadbConfiguration,
                                       @BeanInject(profile = "h2") DataSourceConfiguration h2Configuration,
                                       @BeanInject DdlRepository ddlRepository) {
        this.configuration = configuration;
        this.mariadbConfiguration = mariadbConfiguration;
        this.h2Configuration = h2Configuration;
        this.ddlRepository = ddlRepository;
    }

    public void print() {
        System.out.println(configuration);
        System.out.println(mariadbConfiguration);
        System.out.println(h2Configuration);
        TransactionManager.offer(new TransactionInfo());
        System.out.println(ddlRepository.getTransaction());
    }

    public static void main(String[] args) {
        var applicationFactory = ApplicationFactory.factory(DataSourceConfigurationTest.class)
                .then(applicationContext -> {
                    var beanFactory = applicationContext.getGlobalBeanFactory();
                    // application.start(args);
                    DataSourceConfigurationTest factory = beanFactory.factory(DataSourceConfigurationTest.class);
                    factory.print();
                    DataSourceConfiguration dataSourceConfiguration = beanFactory.factory(DataSourceConfiguration.class);
                    System.out.println(dataSourceConfiguration);
                    DataSourceConfiguration mariadbDataSourceConfiguration = beanFactory.factoryConfiguration(DataSourceConfiguration.class, EnvironmentDepositoryHolder.DEFAULT_PROFILE, "mariadb");
                    System.out.println(mariadbDataSourceConfiguration);
                    DataSourceConfiguration h2DataSourceConfiguration = beanFactory.factoryConfiguration(DataSourceConfiguration.class, EnvironmentDepositoryHolder.DEFAULT_PROFILE, "h2");
                    System.out.println(h2DataSourceConfiguration);
                    System.out.println("---------------------------------------------------------------------------------------");
                    var dateSourceInjection = new BeanInjection<>(DataSourceConfiguration.class);
                    dataSourceConfiguration = applicationContext.factory(dateSourceInjection);
                    System.out.println(dataSourceConfiguration);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
                    dateSourceInjection.addResource("config.profile", EnvironmentDepositoryHolder.DEFAULT_PROFILE);
                    dateSourceInjection.addResource("config.category", "mariadb");
                    dataSourceConfiguration = applicationContext.factory(dateSourceInjection);
                    System.out.println(dataSourceConfiguration);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
                    dateSourceInjection.addResource("config.profile", EnvironmentDepositoryHolder.DEFAULT_PROFILE);
                    dateSourceInjection.addResource("config.category", "h2");
                    dataSourceConfiguration = applicationContext.factory(dateSourceInjection);
                    System.out.println(dataSourceConfiguration);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
                    dateSourceInjection.addResource("config.profile", EnvironmentDepositoryHolder.DEFAULT_PROFILE);
                    dateSourceInjection.addResource("config.category", "mock");
                    dataSourceConfiguration = applicationContext.factory(dateSourceInjection);
                    System.out.println(dataSourceConfiguration);
                    System.out.println("---------------------------------------------------------------------------------------");
                    Set<DataSource> dataSourceSet = beanFactory.getBeanList(DataSource.class);
                    for (DataSource dataSource : dataSourceSet) {
                        System.out.println(dataSource);
                    }
                });
    }
}