package com.truthbean.debbie.example.service;

import com.truthbean.debbie.core.proxy.InterfaceProxyFactory;
import com.truthbean.debbie.example.httpclient.UserHttpClient;
import com.truthbean.debbie.httpclient.HttpClientExecutor;
import com.truthbean.debbie.httpclient.HttpClientProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/04 21:16.
 */
public class RemoteUserService {
    private final Map<Class<?>, InterfaceProxyFactory<UserHttpClient>> knownMappers = new HashMap<>();

    private UserHttpClient userHttpClient;

    public RemoteUserService() {
        var configuration = HttpClientProperties.toConfiguration();
        var type = UserHttpClient.class;
        InterfaceProxyFactory<UserHttpClient> interfaceProxyFactory = knownMappers.get(type);
        if (interfaceProxyFactory == null) {
            interfaceProxyFactory = new InterfaceProxyFactory<>(type, configuration);
            knownMappers.put(type, interfaceProxyFactory);
        }

        userHttpClient = interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }

    public void login() {
        var body = "{\"username\": \"server\",\"password\": \"oceanai\"}";
        List<String> response = userHttpClient.login(body);
        System.out.println(response);
    }
}
