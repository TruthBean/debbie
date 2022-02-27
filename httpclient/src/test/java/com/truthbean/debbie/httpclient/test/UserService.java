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

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.httpclient.HttpClientFactory;
import com.truthbean.debbie.task.DebbieTask;

import java.util.List;

@BeanComponent
public class UserService {

    private final HttpClientFactory httpClientFactory;

    private final UserHttpClient userHttpClient;

    @BeanInject
    private PingService pingService;

    public UserService(@BeanInject HttpClientFactory httpClientFactory, @BeanInject UserHttpClient userHttpClient) {
        this.httpClientFactory = httpClientFactory;
        this.userHttpClient = userHttpClient;
    }


    public UserHttpClient getUserHttpClient() {
        return httpClientFactory.factory(UserHttpClient.class);
    }

    public void login() {
        var body = "{\"username\": \"admin\",\"password\": \"admin\"}";
        List<String> response = userHttpClient.login(body);
        System.out.println(response);
    }

    @DebbieTask(async = false, fixedRate = 1000)
    public void printId() {
        String pong = pingService.ping();
        System.out.println(pong);
    }
}
