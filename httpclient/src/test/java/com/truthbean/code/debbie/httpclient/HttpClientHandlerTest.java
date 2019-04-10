package com.truthbean.code.debbie.httpclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.HttpCookie;
import java.util.List;

public class HttpClientHandlerTest {

    private HttpClientHandler httpClientHandler;

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpClientHandler(properties.toConfiguration());
    }

    @Test
    public void testGet() {
        var body = httpClientHandler.get("http://192.168.1.206:8080/manager/current?token=2-4f29b8e6-aa98-451a-b2c4-24b329df7caf");
        System.out.println(body);

        var cookies = List.of(new HttpCookie("Cookies", "2-4f29b8e6-aa98-451a-b2c4-24b329df7caf"),
                new HttpCookie("Cookies", "2-4f29b8e6-aa98-451a-b2c4-24b329df7caf"));
        body = httpClientHandler.get("http://192.168.1.206:8080/manager/current", cookies);
        System.out.println(body);

        body = httpClientHandler.get("https://www.google.com");
        System.out.println(body);
    }

    @Test
    public void testPost() {
        var body = "{\n" +
                "\t\"username\": \"anonymous\",\n" +
                "\t\"password\": \"anonymous\"\n" +
                "}";
        var response = httpClientHandler.post("http://192.168.1.206:8080/login", body);
        System.out.println(response);
    }
}
