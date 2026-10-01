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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Default {@link WebSocketSession} backed by the AIO
 * {@link AsynchronousSocketChannel} that carried the original HTTP request,
 * which is what allows WebSocket traffic to share the HTTP server port.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
class WebSocketSessionImpl implements WebSocketSession {

    private final String id = UUID.randomUUID().toString();
    private final AsynchronousSocketChannel channel;
    private final String path;
    private final Map<String, String> handshakeHeaders;
    private final long writeTimeout;

    private volatile boolean open = true;
    private final Object writeLock = new Object();

    WebSocketSessionImpl(AsynchronousSocketChannel channel, String path,
                         Map<String, String> handshakeHeaders, long writeTimeout) {
        this.channel = channel;
        this.path = path;
        this.handshakeHeaders = Collections.unmodifiableMap(handshakeHeaders);
        this.writeTimeout = writeTimeout;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getRemoteAddress() {
        try {
            SocketAddress remoteAddress = channel.getRemoteAddress();
            return remoteAddress == null ? "" : remoteAddress.toString();
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public Map<String, String> getHandshakeHeaders() {
        return handshakeHeaders;
    }

    @Override
    public boolean isOpen() {
        return open && channel.isOpen();
    }

    @Override
    public void sendText(String message) {
        send(WebSocketFrame.text(message));
    }

    @Override
    public void sendBinary(byte[] message) {
        send(WebSocketFrame.binary(message));
    }

    @Override
    public void sendPing(byte[] data) {
        send(WebSocketFrame.ping(data));
    }

    void sendPong(byte[] data) {
        send(WebSocketFrame.pong(data));
    }

    void send(WebSocketFrame frame) {
        if (!isOpen()) {
            throw new IllegalStateException("WebSocket session " + id + " is already closed");
        }
        synchronized (writeLock) {
            try {
                WebSocketCodec.writeFrame(channel, frame, writeTimeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                closeQuietly(1006, null);
                throw new IllegalStateException("WebSocket frame write interrupted", e);
            } catch (IOException e) {
                LOGGER.error("Send WebSocket frame failed, session: " + id, e);
                closeQuietly(1006, null);
                throw new IllegalStateException("WebSocket frame write failed", e);
            }
        }
    }

    @Override
    public void close(int statusCode, String reason) {
        if (open) {
            open = false;
            synchronized (writeLock) {
                try {
                    WebSocketCodec.writeFrame(channel, WebSocketFrame.close(statusCode, reason), writeTimeout);
                } catch (IOException | InterruptedException | RuntimeException e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    LOGGER.debug("Write WebSocket close frame failed, session: " + id);
                }
            }
            closeChannel();
        }
    }

    void closeQuietly(int statusCode, String reason) {
        boolean wasOpen = open;
        open = false;
        // only attempt the close handshake echo while the socket is still alive
        if (wasOpen && channel.isOpen()) {
            synchronized (writeLock) {
                try {
                    WebSocketCodec.writeFrame(channel, WebSocketFrame.close(statusCode, reason), 1000);
                } catch (IOException | InterruptedException | RuntimeException e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    LOGGER.debug("Write WebSocket close frame failed, session: " + id);
                }
            }
        }
        closeChannel();
    }

    private void closeChannel() {
        try {
            channel.shutdownOutput();
            channel.shutdownInput();
        } catch (IOException e) {
            LOGGER.trace("WebSocket channel shutdown ignored: " + e.getMessage());
        }
        try {
            channel.close();
        } catch (IOException e) {
            LOGGER.error("WebSocket channel close error, session: " + id, e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSessionImpl.class);
}
