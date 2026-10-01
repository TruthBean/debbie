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

import java.util.Map;

/**
 * The live WebSocket connection view passed to {@link WebSocketEndpoint}
 * callbacks. Sending methods are safe to call from any thread; received
 * messages are dispatched on the connection's AIO thread.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public interface WebSocketSession {

    /**
     * @return a unique identifier of this session
     */
    String getId();

    /**
     * @return the request path the upgrade was performed on
     */
    String getPath();

    /**
     * @return the remote peer address as reported by the socket channel
     */
    String getRemoteAddress();

    /**
     * Upgrade request headers captured during the handshake, keys lower-cased.
     *
     * @return an immutable view of the handshake headers
     */
    Map<String, String> getHandshakeHeaders();

    /**
     * @return {@code true} while the connection is usable
     */
    boolean isOpen();

    /**
     * Sends a text message as a single finished frame.
     *
     * @param message the message text
     * @throws IllegalStateException if the connection is closed or the write fails
     */
    void sendText(String message);

    /**
     * Sends a binary message as a single finished frame.
     *
     * @param message the message content
     * @throws IllegalStateException if the connection is closed or the write fails
     */
    void sendBinary(byte[] message);

    /**
     * Sends a ping frame; a well-behaved peer answers with a pong.
     *
     * @param data optional ping payload, may be {@code null}
     * @throws IllegalStateException if the connection is closed or the write fails
     */
    void sendPing(byte[] data);

    /**
     * Closes the session gracefully with a normal-closure status (1000).
     */
    default void close() {
        close(1000, null);
    }

    /**
     * Closes the session gracefully with the given status code and reason,
     * then releases the underlying channel.
     *
     * @param statusCode the RFC 6455 close status code
     * @param reason     optional close reason, may be {@code null}
     */
    void close(int statusCode, String reason);
}
