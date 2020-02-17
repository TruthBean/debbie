package com.truthbean.debbie.httpclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UserHttpClientTest {

    private HttpClientFactory httpClientFactory = new HttpClientFactory(getClass().getClassLoader());

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
}
