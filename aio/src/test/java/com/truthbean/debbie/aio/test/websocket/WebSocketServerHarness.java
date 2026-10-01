/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio.test.websocket;

import com.truthbean.debbie.aio.websocket.WebSocketEndpointManager;
import com.truthbean.debbie.boot.DebbieApplication;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * End-to-end harness for the WebSocket service sharing the AIO HTTP port.
 * Boots the debbie aio application on port 19099, then drives a real
 * handshake, text/binary echo, ping/pong, graceful close and a concurrent
 * plain HTTP request against the same port. Exits with status 0 on success.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public class WebSocketServerHarness {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        // the echo endpoint is registered through the SPI: module-info provides +
        // META-INF/services, loaded by the aio server during its init phase
        DebbieApplication application = DebbieApplication.create(
                "-Ddebbie.mvc.enable=true",
                "-Ddebbie.server.port=19099");
        application.start();
        Thread.sleep(1000);

        EchoEndpoint echo = (EchoEndpoint) WebSocketEndpointManager.matchEndpoint("/echo");

        StringBuilder textBuf = new StringBuilder();
        ByteArrayOutputStream binaryBuf = new ByteArrayOutputStream();
        CompletableFuture<String> firstText = new CompletableFuture<>();
        java.util.concurrent.atomic.AtomicReference<CompletableFuture<byte[]>> binaryFuture =
                new java.util.concurrent.atomic.AtomicReference<>(new CompletableFuture<>());
        CompletableFuture<byte[]> pingFuture = new CompletableFuture<>();
        WebSocket.Listener listener = new WebSocket.Listener() {
            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                textBuf.append(data);
                if (last) {
                    firstText.complete(textBuf.toString());
                    textBuf.setLength(0);
                }
                webSocket.request(1);
                return null;
            }

            @Override
            public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                byte[] chunk = new byte[data.remaining()];
                data.get(chunk);
                binaryBuf.write(chunk, 0, chunk.length);
                if (last) {
                    binaryFuture.get().complete(binaryBuf.toByteArray());
                    binaryBuf.reset();
                }
                webSocket.request(1);
                return null;
            }

            @Override
            public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
                byte[] bytes = new byte[message.remaining()];
                message.get(bytes);
                pingFuture.complete(bytes);
                webSocket.request(1);
                return null;
            }
        };

        WebSocket webSocket = HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create("ws://127.0.0.1:19099/echo"), listener)
                .get(10, TimeUnit.SECONDS);
        check("handshake opened on http port", echo.getOpened() == 1);

        webSocket.sendText("hello 中文", true);
        String echoed = firstText.get(10, TimeUnit.SECONDS);
        check("text echo", "echo:hello 中文".equals(echoed));

        // register a fresh future before sending, then wait on it
        CompletableFuture<byte[]> binaryWait = new CompletableFuture<>();
        binaryFuture.set(binaryWait);
        webSocket.sendBinary(ByteBuffer.wrap("bytes".getBytes(StandardCharsets.UTF_8)), true);
        String binaryEcho;
        try {
            binaryEcho = new String(binaryWait.get(10, TimeUnit.SECONDS), StandardCharsets.UTF_8);
        } catch (Exception e) {
            binaryEcho = "<FAILED serverMessages=" + echo.getMessages() + " err=" + e + ">";
        }
        if ("echo:bytes".equals(binaryEcho)) {
            check("binary echo", true);
        } else {
            System.out.println("[FAIL] binary echo actual=[" + binaryEcho + "] bytes="
                    + java.util.Arrays.toString(binaryEcho.getBytes(StandardCharsets.UTF_8)));
            failures++;
        }

        // server-initiated ping via the "ping" text command, observed client-side
        webSocket.sendText("ping", true);
        byte[] pingPayload;
        try {
            pingPayload = pingFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            pingPayload = null;
        }
        check("server ping received by client", pingPayload != null && new String(pingPayload, StandardCharsets.UTF_8).equals("srv"));
        // the client auto-answers the ping with a pong handled by the server codec
        Thread.sleep(500);
        check("server received pong", echo.getPongs() >= 1);

        // concurrent plain HTTP request on the very same port while the ws connection is open
        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:19099/api/hello")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        check("http still served on same port", httpResponse.statusCode() > 0);

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
        Thread.sleep(500);
        check("close frame delivered to endpoint", echo.getLastCloseCode() == 1000);

        webSocket.abort();

        if (failures == 0) {
            System.out.println("WEBSOCKET HARNESS: ALL CHECKS PASSED");
            System.exit(0);
        } else {
            System.out.println("WEBSOCKET HARNESS: " + failures + " CHECK(S) FAILED");
            System.exit(1);
        }
    }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
        if (!ok) {
            failures++;
        }
    }
}
