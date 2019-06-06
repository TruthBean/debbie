package com.truthbean.debbie.httpclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UserHttpClientTest {

    private HttpClientFactory httpClientFactory = new HttpClientFactory();

    private UserHttpClient userHttpClient;

    @BeforeEach
    public void before() {
        userHttpClient = httpClientFactory.factory(UserHttpClient.class);
    }

    @Test
    public void testLogin() {
        var body = "{\"username\": \"server\",\"password\": \"oceanai\"}";
        List<String> response = userHttpClient.login(body);
        System.out.println(response);
    }
}
