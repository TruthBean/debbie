package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;

import java.util.List;

@BeanComponent
public class UserService {

    private final HttpClientFactory httpClientFactory;

    private final UserHttpClient userHttpClient;

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
}
