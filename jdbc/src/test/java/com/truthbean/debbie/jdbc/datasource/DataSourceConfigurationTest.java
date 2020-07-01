package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.repository.DdlRepository;

/**
 * @author truthbean/Rogar·Q
 * @since Created on 2020-04-13 13:51.
 */
@DebbieBootApplication
public class DataSourceConfigurationTest {

    @BeanInject
    private DataSourceConfiguration configuration;

    @BeanInject
    private DdlRepository ddlRepository;

    @JdbcTransactional
    public void print() {
        System.out.println(configuration);
        System.out.println(ddlRepository.getDriverConnection().getDriverName());
    }

    public static void main(String[] args) {
        var applicationFactory = DebbieApplicationFactory.configure(DataSourceConfigurationTest.class);
        var beanFactory = applicationFactory.getGlobalBeanFactory();
        // application.start(args);
        DataSourceConfigurationTest factory = beanFactory.factory(DataSourceConfigurationTest.class);
        factory.print();
    }
}