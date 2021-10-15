package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.httpclient.HttpClientHandler;
import com.truthbean.debbie.httpclient.HttpClientProperties;
import com.truthbean.debbie.io.StreamHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class TestAuthLicense {
    private HttpClientHandler httpClientHandler;

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpClientHandler(properties.loadConfiguration());
    }

    @Test
    public void testAuth() throws IOException {
        File file = new File("/Volumes/DOCKER/download/auth.txt");
        String string = Base64.getEncoder().encodeToString(StreamHelper.toByteArray(new FileInputStream(file)));
        String post = httpClientHandler.post("http://192.168.1.12:28080/authority/machine/auth", string);
        System.out.println(post);
    }
}
