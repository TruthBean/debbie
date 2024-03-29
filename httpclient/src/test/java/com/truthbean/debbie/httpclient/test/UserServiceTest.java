/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.core.ApplicationFactory;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private static final UserService userService;

    static {
        var factory = ApplicationFactory.configure(UserServiceTest.class);
        var context = factory.getApplicationContext();

        var beanFactory = context.getGlobalBeanFactory();
        userService = beanFactory.factory(UserService.class);
    }

    @Test
    void getUserHttpClient() {
        System.out.println(userService.getUserHttpClient());
    }

    @Test
    void login() {
        userService.login();
    }
}