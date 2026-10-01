/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio.websocket;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A single WebSocket frame (RFC 6455 section 5). Frames produced by the
 * server side are never masked, so the {@code masked} flag is only meaningful
 * for frames decoded from client connections.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public final class WebSocketFrame {

    private final boolean fin;
    private final WebSocketOpcode opcode;
    private final byte[] payload;
    private final boolean masked;

    WebSocketFrame(boolean fin, WebSocketOpcode opcode, byte[] payload, boolean masked) {
        this.fin = fin;
        this.opcode = opcode;
        this.payload = payload;
        this.masked = masked;
    }

    public boolean isFin() {
        return fin;
    }

    public WebSocketOpcode getOpcode() {
        return opcode;
    }

    public byte[] getPayload() {
        return payload;
    }

    public boolean isMasked() {
        return masked;
    }

    /**
     * Creates a finished text frame carrying the given message encoded as UTF-8.
     *
     * @param text message text
     * @return a TEXT frame
     */
    public static WebSocketFrame text(String text) {
        return new WebSocketFrame(true, WebSocketOpcode.TEXT, text.getBytes(StandardCharsets.UTF_8), false);
    }

    /**
     * Creates a finished binary frame carrying the given payload.
     *
     * @param bytes message content
     * @return a BINARY frame
     */
    public static WebSocketFrame binary(byte[] bytes) {
        return new WebSocketFrame(true, WebSocketOpcode.BINARY, Arrays.copyOf(bytes, bytes.length), false);
    }

    /**
     * Creates a finished PING frame.
     *
     * @param bytes optional ping payload
     * @return a PING frame
     */
    public static WebSocketFrame ping(byte[] bytes) {
        return new WebSocketFrame(true, WebSocketOpcode.PING, bytes == null ? new byte[0] : Arrays.copyOf(bytes, bytes.length), false);
    }

    /**
     * Creates a finished PONG frame.
     *
     * @param bytes the pong payload, usually echoed from a received ping
     * @return a PONG frame
     */
    public static WebSocketFrame pong(byte[] bytes) {
        return new WebSocketFrame(true, WebSocketOpcode.PONG, bytes == null ? new byte[0] : Arrays.copyOf(bytes, bytes.length), false);
    }

    /**
     * Creates a finished CLOSE frame with the given status code and reason.
     *
     * @param statusCode RFC 6455 close status code
     * @param reason     optional close reason, may be {@code null}
     * @return a CLOSE frame
     */
    public static WebSocketFrame close(int statusCode, String reason) {
        byte[] reasonBytes = reason == null ? new byte[0] : reason.getBytes(StandardCharsets.UTF_8);
        byte[] payload = new byte[2 + reasonBytes.length];
        payload[0] = (byte) ((statusCode >> 8) & 0xFF);
        payload[1] = (byte) (statusCode & 0xFF);
        System.arraycopy(reasonBytes, 0, payload, 2, reasonBytes.length);
        return new WebSocketFrame(true, WebSocketOpcode.CLOSE, payload, false);
    }
}
