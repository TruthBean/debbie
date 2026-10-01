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

import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.request.RouterRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Helpers to detect an RFC 6455 opening handshake on the plain HTTP port and
 * to build the {@code 101 Switching Protocols} response. All header lookups
 * are case-insensitive and the {@code Sec-WebSocket-Key} answer is computed
 * with SHA-1 plus the RFC 6455 magic GUID, using only JDK APIs.
 * <p>
 * Only {@link #isWebSocketUpgrade(List)} is meant for use outside this
 * package; the remaining methods are internal to the handshake flow.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public final class WebSocketHandshake {

    /** RFC 6455 section 1.3 magic value appended to the client key */
    private static final String WS_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private WebSocketHandshake() {
    }

    /**
     * Collects the request headers into a case-insensitive (lower-cased key)
     * map. Multi-valued headers are joined with a comma.
     *
     * @param request the parsed HTTP request
     * @return header name (lower case) to value
     */
    static Map<String, String> lowerCaseHeaders(RouterRequest request) {
        Map<String, String> map = new HashMap<>();
        HttpHeader header = request.getHeader();
        if (header != null) {
            Map<String, List<String>> headers = header.getHeaders();
            if (headers != null) {
                headers.forEach((key, values) -> {
                    if (values != null && !values.isEmpty()) {
                        map.put(key.toLowerCase(Locale.ROOT), String.join(",", values));
                    }
                });
            }
        }
        return map;
    }

    /**
     * Checks whether the request carries a syntactically valid WebSocket
     * upgrade handshake: {@code Upgrade: websocket},
     * {@code Connection: ...upgrade...}, a non-blank key and version 13.
     * <p>
     * The handshake headers are sniffed directly from the raw request lines
     * because the regular header parser is lossy (it splits on every colon
     * and only keeps the first value), which must not happen for the
     * case-insensitive WebSocket handshake fields.
     *
     * @param rawLines the raw request lines, request line first
     * @return {@code true} if this is a WebSocket upgrade request
     */
    public static boolean isWebSocketUpgrade(List<String> rawLines) {
        String upgrade = null;
        String connection = null;
        String key = null;
        String version = null;
        for (int i = 1; i < rawLines.size(); i++) {
            String line = rawLines.get(i);
            if (line.isEmpty()) {
                // headers are over, whatever follows is the body
                break;
            }
            int colon = line.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String name = line.substring(0, colon).strip().toLowerCase(Locale.ROOT);
            String value = line.substring(colon + 1).strip();
            switch (name) {
                case "upgrade" -> upgrade = value;
                case "connection" -> connection = value;
                case "sec-websocket-key" -> key = value;
                case "sec-websocket-version" -> version = value;
                default -> {
                }
            }
        }
        if (upgrade == null || !"websocket".equalsIgnoreCase(upgrade)) {
            return false;
        }
        if (connection == null || !containsToken(connection, "upgrade")) {
            return false;
        }
        if (key == null || key.isBlank()) {
            return false;
        }
        return version == null || "13".equals(version);
    }

    private static boolean containsToken(String value, String token) {
        for (String part : value.split(",")) {
            if (token.equalsIgnoreCase(part.strip())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Computes the {@code Sec-WebSocket-Accept} value for the client key.
     *
     * @param key the raw {@code Sec-WebSocket-Key} header value
     * @return the base64 encoded SHA-1 digest of key + magic GUID
     */
    static String computeAccept(String key) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha1.digest((key.strip() + WS_GUID).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-1 is mandated by the JLS, this cannot happen on a JRE
            throw new IllegalStateException("SHA-1 message digest unavailable", e);
        }
    }

    /**
     * Builds the {@code 101 Switching Protocols} response, echoing the accept
     * key and negotiating the first requested sub-protocol when present.
     *
     * @param key      the client {@code Sec-WebSocket-Key}
     * @param protocol the client {@code Sec-WebSocket-Protocol} or {@code null}
     * @param serverMessage the server banner used by the HTTP responses
     * @return the raw handshake response, CRLF terminated
     */
    static String buildUpgradeResponse(String key, String protocol, String serverMessage) {
        StringBuilder response = new StringBuilder("HTTP/1.1 101 Switching Protocols\r\n")
                .append("Upgrade: websocket\r\n")
                .append("Connection: Upgrade\r\n")
                .append("Sec-WebSocket-Accept: ").append(computeAccept(key)).append("\r\n");
        if (protocol != null && !protocol.isBlank()) {
            String selected = protocol.split(",")[0].strip();
            response.append(HttpHeader.HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL.getName())
                    .append(": ").append(selected).append("\r\n");
        }
        if (serverMessage != null && !serverMessage.isBlank()) {
            response.append("Server: ").append(serverMessage).append("\r\n");
        }
        return response.append("\r\n").toString();
    }

    /**
     * Builds a plain HTTP error response used to reject malformed upgrades.
     *
     * @param status HTTP status text, e.g. {@code "400 Bad Request"}
     * @return the raw response, CRLF terminated
     */
    static String buildErrorResponse(String status) {
        byte[] body = status.getBytes(StandardCharsets.UTF_8);
        return "HTTP/1.1 " + status + "\r\n"
                + HttpHeader.HttpHeaderNames.CONTENT_LENGTH.getName() + ": " + body.length + "\r\n"
                + "\r\n" + status;
    }
}
