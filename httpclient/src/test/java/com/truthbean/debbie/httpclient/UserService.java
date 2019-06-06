package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;

import java.util.List;

@BeanComponent
public class UserService {

    @BeanInject
    private HttpClientFactory httpClientFactory;

    @BeanInject
    private UserHttpClient userHttpClient;

    public UserHttpClient getUserHttpClient() {
        return httpClientFactory.factory(UserHttpClient.class);
    }

    public void login() {
        var body = "{\"username\": \"server\",\"password\": \"oceanai\"}";
        List<String> response = userHttpClient.login(body);
        System.out.println(response);
    }
}
