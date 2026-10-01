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

/**
 * Service provider interface for WebSocket endpoints served on the same port
 * as the AIO HTTP server. Implementations are discovered through
 * {@link java.util.ServiceLoader} (declare them in
 * {@code META-INF/services/com.truthbean.debbie.aio.websocket.WebSocketEndpoint})
 * and must provide a public no-argument constructor.
 * <p>
 * Each callback is invoked on the AIO channel-group thread that owns the
 * connection; long-running handling should be dispatched to another executor.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public interface WebSocketEndpoint {

    /**
     * The request path this endpoint is bound to, matched against the upgrade
     * request URI. Supports exact paths and a trailing {@code /*} prefix
     * pattern; {@code /*} alone matches every unmatched upgrade request.
     *
     * @return the bound path, never {@code null}
     */
    String getPath();

    /**
     * Called after the opening handshake succeeded.
     *
     * @param session the newly opened session
     */
    default void onOpen(WebSocketSession session) {
    }

    /**
     * Called when a complete text message has been received (fragments are
     * reassembled before this callback).
     *
     * @param session the current session
     * @param message the text payload
     */
    void onMessage(WebSocketSession session, String message);

    /**
     * Called when a complete binary message has been received.
     *
     * @param session the current session
     * @param message the binary payload
     */
    default void onMessage(WebSocketSession session, byte[] message) {
    }

    /**
     * Called when a pong frame is received, usually in reply to a ping sent
     * via {@link WebSocketSession#sendPing(byte[])}.
     *
     * @param session the current session
     * @param data    the pong payload
     */
    default void onPong(WebSocketSession session, byte[] data) {
    }

    /**
     * Called when the connection is closed by either side.
     *
     * @param session    the current session
     * @param statusCode the RFC 6455 close status code, {@code 1006} for abnormal closure
     * @param reason     the close reason, may be {@code null}
     */
    default void onClose(WebSocketSession session, int statusCode, String reason) {
    }

    /**
     * Called when an error terminates the connection.
     *
     * @param session   the current session, may be {@code null} if the error
     *                  happened before the session was fully established
     * @param throwable the error
     */
    default void onError(WebSocketSession session, Throwable throwable) {
    }
}
