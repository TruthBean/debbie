package com.truthbean.debbie.httpclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SeaweedfsTest {

    private HttpClientHandler httpClientHandler;

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpClientHandler(properties.toConfiguration());
    }

    @Test
    public void testDirLookup() {
        String url = "http://192.168.1.11:31819/dir/lookup?volumeId=3&pretty=y";
        String s = httpClientHandler.get(url);
        System.out.println(s);
    }
}
