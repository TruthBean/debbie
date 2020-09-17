/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.httpclient.HttpClientProperties;
import com.truthbean.debbie.httpclient.HttpConnectionHandler;
import com.truthbean.debbie.httpclient.form.FileFormDataParam;
import com.truthbean.debbie.httpclient.seaweedfs.DirAssignResponse;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.util.JacksonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.HttpCookie;
import java.util.List;

public class HttpConnectionHandlerTest {
    private HttpConnectionHandler httpClientHandler;

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpConnectionHandler(properties.loadConfiguration());
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

    /*@Test
    public void testUploadFile() {
        var url = "http://localhost:12005/file/upload";
        httpClientHandler.post()
    }*/

    @Test
    public void testSeaweedfsDirAssign() {
        var seaWeedFSUrl = "http://192.168.1.12:9333";
        var dirAssign = seaWeedFSUrl + "/dir/assign";
        String response = httpClientHandler.get(dirAssign);
        System.out.println(response);

        DirAssignResponse assign = JacksonUtils.jsonToBean(response, DirAssignResponse.class);
        System.out.println(assign);
        String url = "http://" + assign.getPublicUrl() + "/" + assign.getFid();
        FileFormDataParam param = new FileFormDataParam();
        param.setName("file");
        param.setFile(new File("F:\\data\\images\\童思雨.jpg"));

        String form = httpClientHandler.form(url, List.of(param));
        System.out.println(form);
    }
}
