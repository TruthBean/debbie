package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.core.proxy.InterfaceProxyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHttpClientTest {

    private final Map<Class<?>, InterfaceProxyFactory<UserHttpClient>> knownMappers = new HashMap<>();

    private UserHttpClient userHttpClient;

    @BeforeEach
    public void before() {
        var configuration = new HttpClientProperties().toConfiguration();
        var type = UserHttpClient.class;
        InterfaceProxyFactory<UserHttpClient> interfaceProxyFactory = knownMappers.get(type);
        if (interfaceProxyFactory == null) {
            interfaceProxyFactory = new InterfaceProxyFactory<>(type, configuration);
            knownMappers.put(type, interfaceProxyFactory);
        }

        userHttpClient = interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }

    @Test
    public void testLogin() {
        var body = "{\"username\": \"server\",\"password\": \"oceanai\"}";
        List<String> response = userHttpClient.login(body);
        System.out.println(response);
    }
}
