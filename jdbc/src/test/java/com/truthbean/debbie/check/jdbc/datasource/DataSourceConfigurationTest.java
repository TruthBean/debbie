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

/**
 * @author truthbean/Rogar·Q
 * @since Created on 2020-04-13 13:51.
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
@JdbcTransactional
public class DataSourceConfigurationTest {

    private final DataSourceConfiguration configuration;

    private final DdlRepository ddlRepository;

    public DataSourceConfigurationTest(@BeanInject DataSourceConfiguration configuration,
                                       @BeanInject DdlRepository ddlRepository) {
        this.configuration = configuration;
        this.ddlRepository = ddlRepository;
    }

    public void print() {
        System.out.println(configuration);
        System.out.println(ddlRepository.getTransaction().getDriverName());
    }

    public static void main(String[] args) {
        var applicationFactory = ApplicationFactory.configure(DataSourceConfigurationTest.class);
        var context = applicationFactory.getApplicationContext();
        var beanFactory = context.getGlobalBeanFactory();
        // application.start(args);
        DataSourceConfigurationTest factory = beanFactory.factory(DataSourceConfigurationTest.class);
        factory.print();
    }
}