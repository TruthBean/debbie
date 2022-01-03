/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.datasource;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.repository.DdlRepository;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * @author truthbean/Rogar·Q
 * @since Created on 2020-04-13 13:51.
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
@JdbcTransactional
public class DataSourceConfigurationTest {

    static {
        System.setProperty("logging.level.com.truthbean.debbie", "TRACE");
    }

    private final DataSourceConfiguration configuration;
    private final DataSourceConfiguration mariadbConfiguration;
    private final DataSourceConfiguration h2Configuration;

    private final DdlRepository ddlRepository;

    public DataSourceConfigurationTest(@BeanInject DataSourceConfiguration configuration,
                                       @BeanInject(name = "mariadb") DataSourceConfiguration mariadbConfiguration,
                                       @BeanInject(name = "h2") DataSourceConfiguration h2Configuration,
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
        var applicationFactory = ApplicationFactory.factory(DataSourceConfigurationTest.class);
        var context = applicationFactory.getApplicationContext();
        var beanFactory = context.getGlobalBeanFactory();
        // application.start(args);
        DataSourceConfigurationTest factory = beanFactory.factory(DataSourceConfigurationTest.class);
        factory.print();
    }
}