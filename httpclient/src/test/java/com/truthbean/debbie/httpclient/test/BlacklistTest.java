package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.httpclient.form.FileFormDataParam;
import com.truthbean.debbie.httpclient.form.FormDataParam;
import com.truthbean.debbie.mvc.request.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 1.0.1
 * Created on 2021-05-24 16:10
 */
public class BlacklistTest {

    @Test
    public void testList() {
        byte[] body = "{}".getBytes(StandardCharsets.ISO_8859_1);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String response = ClientAuthService.http("/blacklist/get", HttpMethod.POST, new HashMap<>(), headers, body, System.currentTimeMillis());
        System.out.println(response);
    }

    @Test
    public void testDelete() {
        byte[] body = "{}".getBytes(StandardCharsets.ISO_8859_1);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String response = ClientAuthService.http("/blacklist/1", HttpMethod.DELETE, new HashMap<>(), headers, body, System.currentTimeMillis());
        System.out.println(response);
    }

    @Test
    public void testVideoLoad() {
        byte[] body = "{}".getBytes(StandardCharsets.ISO_8859_1);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String response = ClientAuthService.http("/videoload", HttpMethod.POST, new HashMap<>(), headers, body, System.currentTimeMillis());
        System.out.println(response);
    }

    @Test
    public void testImportExcel() {
        List<FormDataParam> params = new ArrayList<>();
        FileFormDataParam param = new FileFormDataParam();
        param.setName("excel");
        param.setFile(new File("F:\\data\\images\\tongsiyu.jpg"));
        params.add(param);

        String response = ClientAuthService.form("/blacklist/excel", HttpMethod.POST, new HashMap<>(), new HashMap<>(), params, System.currentTimeMillis());
        System.out.println(response);
        System.out.println("------------------------------");
    }
}