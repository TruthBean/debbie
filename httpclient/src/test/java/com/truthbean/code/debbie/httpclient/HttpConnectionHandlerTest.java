package com.truthbean.code.debbie.httpclient;

import com.truthbean.code.debbie.core.io.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.HttpCookie;
import java.util.List;

public class HttpConnectionHandlerTest {
    private HttpConnectionHandler httpClientHandler;

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpConnectionHandler(properties.toConfiguration());
    }

    @Test
    public void testGet() {
        var body = httpClientHandler.get("http://192.168.1.206:8080/manager/current?token=2-09ca573e-2d9a-4389-aa45-1da1d48ca353");
        System.out.println(body);

        var cookies = List.of(new HttpCookie("Cookies", "2-09ca573e-2d9a-4389-aa45-1da1d48ca353"),
                new HttpCookie("Cookies", "2-09ca573e-2d9a-4389-aa45-1da1d48ca353"));
        body = httpClientHandler.get("http://192.168.1.206:8080/manager/current", cookies);
        System.out.println(body);

        body = httpClientHandler.get("https://www.google.com");
        System.out.println(body);
    }

    @Test
    public void testPost() {
        var body = "{\n" +
                "\t\"username\": \"user\",\n" +
                "\t\"password\": \"password\"\n" +
                "}";
        var response = httpClientHandler.post("http://192.168.1.206:8080/login", body, MediaType.APPLICATION_JSON_UTF8);
        System.out.println(response);
    }
}
