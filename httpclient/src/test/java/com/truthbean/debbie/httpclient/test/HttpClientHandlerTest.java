/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.test;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.httpclient.HttpClientHandler;
import com.truthbean.debbie.httpclient.HttpClientProperties;
import com.truthbean.debbie.httpclient.model.Ticket;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.jackson.util.JacksonUtils;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.response.ResponseEntity;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.tools.ChineseNameHelper;
import com.truthbean.tools.ChineseNationalIdHelper;
import com.truthbean.tools.ChinesePhoneNumberHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.util.*;

public class HttpClientHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandlerTest.class);

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
                map1.put("manufacturer", "武汉");
                map1.put("gpsx", 114.5076953435);
                map1.put("gpsy", 30.45700912343);
                map1.put("region_id", 7);
                map1.put("status", 1);
                map1.put("grab_choice", "rtsp");
                map1.put("threshold", 75);
                map1.put("framerate", 1);
                map1.put("face_size", 60);
                map1.put("tracking", 1);

                System.out.println(line);
                String[] split = line.split("\\s+");
                map1.put("name", split[1]);
                map1.put("address", split[0]);
                map1.put("api_address", "");
                map1.put("web_hook", "");

                if (split.length == 2) {
                    map1.put("type", "卫视直播");
                } else
                    map1.put("type", split[2]);

                List<Map<String, Object>> libraryIds = new ArrayList<>();
                Map<String, Object> libraryInfo1 = new LinkedHashMap<>();
                libraryInfo1.put("libraryId", 2);
                libraryIds.add(libraryInfo1);

                Map<String, Object> libraryInfo2 = new LinkedHashMap<>();
                libraryInfo2.put("libraryId", 4);
                libraryIds.add(libraryInfo2);

                Map<String, Object> libraryInfo3 = new LinkedHashMap<>();
                libraryInfo3.put("libraryId", 6);
                libraryIds.add(libraryInfo3);

                Map<String, Object> libraryInfo4 = new LinkedHashMap<>();
                libraryInfo4.put("libraryId", 8);
                libraryIds.add(libraryInfo4);

                map1.put("libraryInfos", libraryIds);
                map1.put("deploymentIds", null);
                map1.put("door_id", 0);

                data.add(map1);
            }
        }


        for (Map<String, Object> datum : data) {
            var body = JacksonUtils.toJson(datum);
            System.out.println(body);

            var header = new HashMap<String, String>();
            header.put("Authorization", "8b409c54-eace-47a9-b8be-db96c158ffff");
            header.put("Content-Type", "application/json;charset=UTF-8");
            var response = httpClientHandler.post("http://192.168.1.11:4002/cameras", body, header);
            System.out.println(response);
        }

    }

    @Test
    public void delete() {
        for (int i = 0; i < 12; i++) {
            var header = new HashMap<String, String>();
            header.put("Authorization", "9bef6a59-688e-49b6-8da8-ce31582e7e52");
            var r = httpClientHandler.delete("http://192.168.1.11:4002/cameras/" + (i + 51), header);
            System.out.println(r);
        }
    }

    @Test
    public void testDelete() {
        String delete = httpClientHandler.delete("http://192.168.1.11:9333/551,041a097220d2359e");
        LOGGER.info(delete);
        LOGGER.info("-----------------------------------");
    }

    @Test
    public void ping() {
        var ping = httpClientHandler.get("http://192.168.1.11:8098/ping");
        System.out.println(ping);
    }

    @Test
    public void ticketList() {
        for (int i = 0; i < 100; i++) {
            var ticketList = httpClientHandler.get("http://192.168.1.139:8098/ticket/list?pageSize=100");
            @SuppressWarnings("unchecked")
            ResponseEntity<Ticket[]> responseEntity =
                    JacksonUtils.jsonToParametricBean(ticketList, ResponseEntity.class, Ticket[].class);
            if (responseEntity != null && responseEntity.getData() != null) {
                // System.out.println(ticketList);
                Random random = new Random();

                var data = responseEntity.getData();
                for (var ticket : data) {
                    ticket.setId(null);
                    ticket.setIdNumber(ChineseNationalIdHelper.generateRandomId());
                    ticket.setName(ChineseNameHelper.getRandomChineseName());
                    ticket.setPhoneNumber(ChinesePhoneNumberHelper.getRandomPhoneNumber());
                    ticket.setSeatId(random.nextInt(28) + 1);
                    ticket.setCarriageId(1);
                    ticket.setLeaveScheduleId(random.nextInt(8) + 1);
                }

                Map<String, String> header = new HashMap<>();
                header.put(HttpHeader.HttpHeaderNames.CONTENT_TYPE.getName(), MediaType.APPLICATION_JSON_UTF8.getValue());
                var json = JacksonUtils.toJson(data);
                // var json = responseEntity.getData().toString();
                // var json = "{\"id\":1,\"name\":\"张思\",\"phoneNumber\":\"13444441234\",\"idNumber\":\"420000199605206600\",\"trainId\":1,\"carriageId\":1,\"leaveScheduleId\":2,\"arriveScheduleId\":6,\"seatId\":2,\"image\":\"70,095963b0018993\",\"feature\":[],\"createTime\":1594973736000,\"updateTime\":1594973736000,\"date\":\"2020年01月01日\",\"dateValue\":1577808000000,\"trainScheduleId\":null,\"trainName\":\"X196\",\"startingStationId\":null,\"startingStation\":null,\"leaveStationId\":4,\"leaveStationName\":\"淄博\",\"leaveStationTime\":\"2020年01月01日 11:05\",\"leaveStationTimeValue\":1577847900000,\"arriveStationId\":7,\"arriveStationName\":\"天津南\",\"arriveStationTime\":\"2020年01月01日 12:30\",\"arriveStationTimeValue\":1577853000000,\"terminalStationId\":null,\"terminalStation\":null,\"time\":\"11:05\",\"timeValue\":1577847900000,\"carriageName\":\"01车\",\"seatName\":\"02A号\",\"createTimestamp\":\"2020-07-17 16:15:36.0\",\"updateTimestamp\":\"2020-07-17 16:15:36.0\",\"deleted\":null}";
                var sync = httpClientHandler.post("http://192.168.1.139:8098/ticket/sync", json, header);
                // System.out.println(sync);
            }
        }
    }
}
