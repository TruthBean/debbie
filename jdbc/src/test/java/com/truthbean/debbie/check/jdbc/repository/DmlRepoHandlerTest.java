/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
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
@DebbieApplicationTest
public class DmlRepoHandlerTest {

    @Test
    public void testInsert(@BeanInject("surnameRepository") SurnameRepository surnameRepository)
            throws MalformedURLException {
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
    public void testFindById(@BeanInject("surnameRepository") SurnameRepository surnameRepository) {
        Optional<Surname> surname = surnameRepository.findById(2L);
        System.out.println(surname);
    }

    @Test
    public void testTransaction(@BeanInject("surnameRepository") SurnameRepository surnameRepository)
            throws MalformedURLException {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("姬");
        q.setWebsite(new URL("https://www.ye.org"));
        q.setName("叶");
        var result = surnameRepository.saveAndDelete(q, 2L);
        System.out.println(result);
    }

    @Test
    public void testUpdate(@BeanInject("surnameRepository") SurnameRepository surnameRepository)
            throws MalformedURLException {
        Optional<Surname> surnameOptional = surnameRepository.findById(1L);
        Surname surname = surnameOptional.get();
        System.out.println(surname);
        surname.setWebsite(new URL("https://qu.org"));
        var b = surnameRepository.update(surname);
        System.out.println(b);
        System.out.println(surname);
    }

    @Test
    public void testDeleteById(@BeanInject("surnameRepository") SurnameRepository surnameRepository) {
        var b = surnameRepository.delete(1L);
        System.out.println(b);
    }

    @Test
    public void findList(@BeanInject("surnameRepository") SurnameRepository surnameRepository) {
        var l = surnameRepository.findAll();
        try {
            System.out.println(l.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void count(@BeanInject("surnameRepository") SurnameRepository surnameRepository) {
        var l = surnameRepository.count();
        System.out.println(l);
    }

    @Test
    public void findPaged(@BeanInject("surnameRepository") SurnameRepository surnameRepository) {
        var l = surnameRepository.findPaged(PageRequest.of(0, 10));
        System.out.println(l);
    }

    @Test
    public void existsById(@BeanInject("surnameRepository") SurnameRepository surnameRepository) {
        Boolean exists = surnameRepository.exists(4L);
        System.out.println(exists);
    }
}
