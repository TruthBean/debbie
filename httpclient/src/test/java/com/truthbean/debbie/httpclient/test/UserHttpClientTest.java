/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.httpclient.HttpClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UserHttpClientTest {

    private final HttpClientFactory httpClientFactory = new HttpClientFactory(getClass().getClassLoader());

    private UserHttpClient userHttpClient;

    @BeforeEach
    public void before() {
        userHttpClient = httpClientFactory.factory(UserHttpClient.class);
    }

    @Test
    public void testLogin() {
        var body = "{\"username\": \"admin\",\"password\": \"admin\"}";
        List<String> response = userHttpClient.login(body);
        System.out.println(response);
    }

    @Test
    public void test() {
        String s = userHttpClient.toString();
        System.out.println("toString: " + s);
        Class<? extends UserHttpClient> clas = userHttpClient.getClass();
        System.out.println("getClass: " + clas);
        int i = userHttpClient.hashCode();
        System.out.println("hashCode: " + i);
    }
}
