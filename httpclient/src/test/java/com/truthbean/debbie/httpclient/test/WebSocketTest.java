/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.httpclient.ws.WebSocketListener;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-22 10:22
 */
public class WebSocketTest {

    @Test
    void test() throws URISyntaxException {
        var httpClient = HttpClient.newBuilder().build();
        CompletableFuture<WebSocket> webSocket = httpClient
                .newWebSocketBuilder()
                .buildAsync(new URI("ws://192.168.1.13:4087/mix/-1?Auth-Ak=oceanai&Auth-Hash=oceanai&Hash-Time=1623314428474"), new WebSocketListener());
        webSocket.join();
        while (true);
    }

    @Test
    void testBinary() throws URISyntaxException {
        var httpClient = HttpClient.newBuilder().build();
        CompletableFuture<WebSocket> webSocket = httpClient
                .newWebSocketBuilder()
                .buildAsync(new URI("ws://192.168.1.13:7777/351?addr=rtsp://admin:iec123456@192.168.1.71:554/h264/ch5/main/av_stream&method=single&faceSize=40&Authorization=8aa7c179-f549-4623-a170-1240ee8de91b"), new WebSocketListener());
        webSocket.join();
        while (true);
    }
}
