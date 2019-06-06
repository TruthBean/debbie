package com.truthbean.debbie.example.service;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.example.httpclient.UserHttpClient;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/04 21:16.
 */
@BeanComponent
public class RemoteUserService {

    @BeanInject
    private UserHttpClient userHttpClient;

    public List<String> login() {
        var body = "{\"username\": \"server\",\"password\": \"oceanai\"}";
        return userHttpClient.login(body);
    }
}
