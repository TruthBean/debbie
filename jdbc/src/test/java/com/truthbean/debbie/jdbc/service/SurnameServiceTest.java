/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.check.jdbc.datasource.DataSourceConfigurationTest;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

class SurnameServiceTest {

    private static SurnameService surnameService;

    @BeforeAll
    static void before() {
        var factory = ApplicationFactory.create(DataSourceConfigurationTest.class);

        ApplicationContext applicationContext = factory.getApplicationContext();
        var beanFactory = applicationContext.getGlobalBeanFactory();
        surnameService = beanFactory.factory("surnameService");
    }

    // @Test
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
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(surnameService.list());
                System.out.println("--------------------------------------");
            }).start();
        }
    }

    @Test
    void doNothing() {
        surnameService.doNothing();
    }

}