/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

@ExtendWith({DebbieApplicationExtension.class})
class SurnameServiceBeanTest {

    @Test
    void save(@BeanInject("surnameService") SurnameService surnameService) throws MalformedURLException {
        var q = new Surname();
        q.setId(27L);
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("1");
        q.setWebsite(new URL("https://www.qu.org"));
        q.setName("屈");
        var b = surnameService.save(q);
        System.out.println(b);
        System.out.println(q);

        var z = new Surname();
        z.setBegin(new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        z.setOrigin("");
        z.setWebsite(new URL("https://www.zhao.org"));
        z.setName("赵");
        var bz = surnameService.save(z);
        System.out.println(bz);
        System.out.println(z);
    }

    @Test
    void selectById(@BeanInject("surnameService") SurnameService surnameService) {
        System.out.println(surnameService.selectById(2L));
    }

    @Test
    void selectAll(@BeanInject("surnameService") SurnameService surnameService) {
        System.out.println("---------------------");
        System.out.println(surnameService.selectById(1L));
    }

    @Test
    void doNothing(@BeanInject("surnameService") SurnameService surnameService) {
        surnameService.doNothing();
    }

    @Test
    public void select(@BeanInject ApiService apiService) {
        System.out.println(apiService.selectAll());
    }

}