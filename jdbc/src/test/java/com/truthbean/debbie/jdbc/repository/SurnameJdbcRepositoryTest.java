/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-02-27 12:46
 */
@ExtendWith({DebbieApplicationExtension.class})
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
    }

    @Test
    void findPaged() {
    }
}