package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfigurationTest;
import com.truthbean.debbie.jdbc.entity.Surname;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

class SurnameServiceTest {

    private static SurnameService surnameService;

    @BeforeAll
    static void before() {
        var factory = DebbieApplicationFactory.configure(DataSourceConfigurationTest.class);

        BeanFactoryHandler beanFactoryHandler = factory.getBeanFactoryHandler();
        surnameService = beanFactoryHandler.factory("surnameService");
    }

    @Test
    void save() throws MalformedURLException {
        var q = new Surname();
        q.setId(27L);
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("1");
        q.setWebsite(new URL("https://www.zhu.org"));
        q.setName("zhu");
        var b = surnameService.save(q);
        System.out.println(b);
        System.out.println(q);
    }

    @Test
    void selectById() {
        System.out.println(surnameService.selectById(2L));
    }

    @Test
    void selectAll() {
        System.out.println(surnameService.list());
    }

    @Test
    void doNothing() {
        surnameService.doNothing();
    }

}