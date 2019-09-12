package com.truthbean.debbie.net.uri;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

class UriUtilsTest {

    @Test
    public void test() throws MalformedURLException {
        String url = "https://user:password@debbie.truthbean.com/hello/hahahah.action.do;matrix1=value&matrix2=中文;matrix1=value3;=hahah/hahaha?key1=name&key1=?name&key1=name&key2=2&key3=3.45&4=#/home/user#;matrix1=value?value=1";
        var queriesInUri = UriUtils.queriesInUri(url);
        System.out.println(queriesInUri.toString());

        System.out.println(UriUtils.getScheme(url));

        System.out.println("------------------------");
        var encodedUrl = URLEncoder.encode(url, Charset.forName("GBK"));
        System.out.println(encodedUrl);
        var uri = UriUtils.resolveUrl(new URL(url));
        System.out.println(uri);
    }

}