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

import com.truthbean.debbie.aio.websocket.WebSocketEndpoint;
import com.truthbean.debbie.aio.websocket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Echo endpoint used by the WebSocket end-to-end harness: mirrors text and
 * binary messages back to the client and counts every interaction.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public class EchoEndpoint implements WebSocketEndpoint {

    private final AtomicInteger opened = new AtomicInteger();
    private final AtomicInteger messages = new AtomicInteger();
    private final AtomicInteger pongs = new AtomicInteger();
    private volatile int lastCloseCode = -1;

    @Override
    public String getPath() {
        return "/echo";
    }

    @Override
    public void onOpen(WebSocketSession session) {
        opened.incrementAndGet();
    }

    @Override
    public void onMessage(WebSocketSession session, String message) {
        messages.incrementAndGet();
        if ("ping".equals(message)) {
            // test helper: server-initiated ping so the client can observe it
            session.sendPing("srv".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return;
        }
        session.sendText("echo:" + message);
    }

    @Override
    public void onMessage(WebSocketSession session, byte[] message) {
        messages.incrementAndGet();
        byte[] prefix = "echo:".getBytes(StandardCharsets.UTF_8);
        byte[] reply = new byte[message.length + prefix.length];
        System.arraycopy(prefix, 0, reply, 0, prefix.length);
        System.arraycopy(message, 0, reply, prefix.length, message.length);
        session.sendBinary(reply);
    }

    @Override
    public void onPong(WebSocketSession session, byte[] data) {
        pongs.incrementAndGet();
    }

    @Override
    public void onClose(WebSocketSession session, int statusCode, String reason) {
        lastCloseCode = statusCode;
    }

    public int getOpened() {
        return opened.get();
    }

    public int getMessages() {
        return messages.get();
    }

    public int getPongs() {
        return pongs.get();
    }

    public int getLastCloseCode() {
        return lastCloseCode;
    }
}
