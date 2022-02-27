/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.repository.DynamicRepository;
import com.truthbean.debbie.jdbc.repository.JdbcRepositoryHandler;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-02-27 12:46
 */
@DebbieApplicationTest
class SurnameJdbcRepositoryTest {

    @BeanInject
    private SurnameJdbcRepository repository;

    @BeforeEach
    public void before(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        TransactionManager.offer(factory.getTransaction());
    }

    @Test
    void insert() {
    }

    @Test
    void saveAndDelete() {
    }

    @Test
    void selectById() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void findAll() {
        List<Surname> all = repository.findAll();
        System.out.println(all);
    }

    @Test
    void count() {
        Optional<Integer> count = DynamicRepository.queryTransactional()
                .select().count().from("surname")
                .single(LOGGER, new JdbcRepositoryHandler(), EntityResolver.getInstance(), int.class);
        System.out.println(count);
    }

    @Test
    void findPaged() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SurnameJdbcRepositoryTest.class);
}