/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.check.jdbc.datasource.DataSourceConfigurationTest;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 21:57.
 */
class DmlRepositoryHandlerTest {
    private static SurnameRepository surnameRepository;

    @BeforeAll
    public static void before() {
        var applicationFactory = ApplicationFactory.configure(DataSourceConfigurationTest.class);
        var context = applicationFactory.getApplicationContext();

        ApplicationContext applicationContext = applicationFactory.getApplicationContext();
        var beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.register(Surname.class);

        var config = DataSourceProperties.toConfiguration();
        DataSourceFactory factory = new DefaultDataSourceFactory();
        factory.factory(config);

        surnameRepository = context.factory(SurnameRepository.class);
    }

    @Test
    void testInsert() throws MalformedURLException {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("姬");
        q.setWebsite(new URL("https://www.qu.org"));
        q.setName("璩");
        var b = surnameRepository.save(q);
        System.out.println(b);
        System.out.println(q);
    }

    @Test
    void testFindById() {
        Optional<Surname> surname = surnameRepository.findById(2L);
        System.out.println(surname);
    }

    @Test
    void testTransaction() throws MalformedURLException {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("姬");
        q.setWebsite(new URL("https://www.ye.org"));
        q.setName("叶");
        var result = surnameRepository.saveAndDelete(q, 2L);
        System.out.println(result);
    }

    @Test
    void testUpdate() throws MalformedURLException {
        Optional<Surname> surnameOptional = surnameRepository.findById(1L);
        Surname surname = surnameOptional.get();
        System.out.println(surname);
        surname.setWebsite(new URL("https://qu.org"));
        var b = surnameRepository.update(surname);
        System.out.println(b);
        System.out.println(surname);
    }

    @Test
    void testDeleteById() {
        var b = surnameRepository.delete(1L);
        System.out.println(b);
    }

    @Test
    void findList() {
        var l = surnameRepository.findAll();
        try {
            System.out.println(l.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
    void count() {
        var l = surnameRepository.count();
        System.out.println(l);
    }

    @Test
    void findPaged() {
        var l = surnameRepository.findPaged(PageRequest.of(0, 10));
        System.out.println(l);
    }

    @Test
    void existsById() {
        Boolean exists = surnameRepository.exists(4L);
        System.out.println(exists);
    }
}
