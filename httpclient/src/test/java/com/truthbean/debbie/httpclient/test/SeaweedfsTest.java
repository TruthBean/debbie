/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.httpclient.HttpClientHandler;
import com.truthbean.debbie.httpclient.HttpClientProperties;
import com.truthbean.debbie.httpclient.HttpResponseType;
import com.truthbean.debbie.httpclient.form.FileFormDataParam;
import com.truthbean.debbie.httpclient.seaweedfs.DirAssignResponse;
import com.truthbean.debbie.jackson.util.JacksonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SeaweedfsTest {

    private HttpClientHandler httpClientHandler;

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpClientHandler(properties.loadConfiguration());
    }

    @Test
    public void testDirLookup() {
        String url = "http://192.168.1.11:9333/dir/lookup?volumeId=3&pretty=y";
        String s = httpClientHandler.get(url);
        System.out.println(s);
    }

    @Test
    public void delete() {
        var seaWeedFSUrl = "http://192.168.1.11:9333/col/delete?collection=";
        String day;
        for (int i = 0; i <= 31; i++) {
            if (i < 10) {
                day = "0" + i;
            } else {
                day = String.valueOf(i);
            }
            httpClientHandler.get(seaWeedFSUrl + "new_sanning_202112" + day);
        }
    }

    @Test
    public void testSeaweedfsDirAssign() {
        var seaWeedFSUrl = "http://192.168.1.11:9333";
        var dirAssign = seaWeedFSUrl + "/dir/assign";
        String response = httpClientHandler.get(dirAssign);
        System.out.println(response);

        DirAssignResponse assign = JacksonUtils.jsonToBean(response, DirAssignResponse.class);
        System.out.println(assign);
        String url = "http://" + assign.getPublicUrl() + "/" + assign.getFid();
        FileFormDataParam param = new FileFormDataParam();
        param.setName("123456");
        param.setFile(new File("F:\\data\\images\\tongsiyu.jpg"));

        String boundary = Long.toHexString(System.currentTimeMillis());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        httpClientHandler.buildForm(List.of(param), boundary, output);

        String contentType = "multipart/form-data; boundary=" + boundary;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);

        String form = httpClientHandler.post(url, output.toByteArray(), headers);
        System.out.println(form);
    }

    public static void main(String[] args) {
        SeaweedfsTest test = new SeaweedfsTest();
        var properties = new HttpClientProperties();
        test.httpClientHandler = new HttpClientHandler(properties.loadConfiguration());
        for (int i = 3897; i <= 4673; i += 10) {
            String url = "http://192.168.1.11:2002/peoples";
            Map<String, String> head = new Hashtable<>();
            head.put("Authorization", "fb520b01-10d6-454e-a98f-6634f3c6b469");
            head.put("Content-Type", "application/json;charset=utf-8");
            head.put("Accept", "application/json, text/plain, */*");
            StringBuilder body = new StringBuilder("[{\"groupId\":2,\"ids\":[");
            for (int j = 0; j < 9; j++) {
                body.append(i + j).append(",");
            }
            body.append(i + 9).append("]}]");
            String delete = test.httpClientHandler.delete(url, body.toString(), head, HttpResponseType.STRING);
            System.out.println(delete);
        }

        // String url = "http://192.168.1.8:31202/blacklist";

    }
}
