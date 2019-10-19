package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.JacksonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpCookie;
import java.util.*;

public class HttpClientHandlerTest {

    private HttpClientHandler httpClientHandler;

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpClientHandler(properties.loadConfiguration());
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

    @Test
    public void addCamera() throws IOException {
        List<Map<String, Object>> data = new LinkedList<>();

        InputStream inputStream = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream("test.txt");
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (!line.isBlank()) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("manufacturer", "湖北");
                map1.put("gpsx", 114.507695);
                map1.put("gpsy", 30.457009);
                map1.put("region_id", 1);
                map1.put("status", 1);
                map1.put("grab_choice", "rtsp");
                map1.put("threshold", 75);
                map1.put("framerate", 1);
                map1.put("face_size", "80");
                map1.put("tracking", "0");

                System.out.println(line);
                String[] split = line.split("\\s+");
                map1.put("name", split[1]);
                map1.put("address", split[0]);

                if (split.length == 2) {
                    map1.put("type", "卫视直播");
                } else
                    map1.put("type", split[2]);

                Map<String, Object> libraryInfo1 = new LinkedHashMap<>();
                libraryInfo1.put("libraryId", 2);
                map1.put("libraryInfos", List.of(libraryInfo1));
                map1.put("deploymentIds", null);
                map1.put("door_id", "0");

                data.add(map1);
            }
        }


        for (Map<String, Object> datum : data) {
            var body = JacksonUtils.toJson(datum);
            System.out.println(body);

            var header = new HashMap<String, String>();
            header.put("Authorization", "b34c891c-76ca-4c8d-9a85-0771b1cbcd1d");
            header.put("Content-Type", "application/json;charset=UTF-8");
            var response = httpClientHandler.post("http://192.168.1.2:31202/cameras", body, header);
            System.out.println(response);
        }

    }

    @Test
    public void delete() {
        for (int i = 0; i < 100; i++) {
            var header = new HashMap<String, String>();
            header.put("Authorization", "b34c891c-76ca-4c8d-9a85-0771b1cbcd1d");
            var r = httpClientHandler.delete("http://192.168.1.2:31202/cameras/" + (i), header);
            System.out.println(r);
        }
    }
}
