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
import com.truthbean.debbie.aio.AioServerConfiguration;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.request.RouterRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Drives one WebSocket connection that started as an ordinary HTTP request on
 * the server port: performs the opening handshake, then loops reading frames
 * and dispatches them to the matched {@link WebSocketEndpoint}.
 * <p>
 * Runs on the AIO channel-group thread that read the original request, so it
 * holds no extra thread of its own; the connection stays open until a close
 * frame, an idle read timeout (see {@code debbie.server.aio.websocket.read.timeout})
 * or an I/O error terminates the loop.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public class WebSocketHandler implements Runnable {

    /** close code signalling an abnormal closure without a close frame */
    private static final int CLOSE_ABNORMAL = 1006;
    /** close code for a protocol violation */
    private static final int CLOSE_PROTOCOL_ERROR = 1002;

    private final AsynchronousSocketChannel channel;
    private final RouterRequest request;
    private final WebSocketEndpoint endpoint;
    private final AioServerConfiguration configuration;
    private final Map<String, String> handshakeHeaders;

    private WebSocketSessionImpl session;
    private int closeCode = 1000;
    private String closeReason;

    /**
     * Creates a handler for an upgrade request.
     *
     * @param channel       the connection that carried the HTTP upgrade request
     * @param request       the parsed handshake request
     * @param endpoint      the endpoint matched for the request path
     * @param configuration the AIO server configuration (same port, charset...)
     */
    public WebSocketHandler(AsynchronousSocketChannel channel, RouterRequest request,
                            WebSocketEndpoint endpoint, AioServerConfiguration configuration) {
        this.channel = channel;
        this.request = request;
        this.endpoint = endpoint;
        this.configuration = configuration;
        this.handshakeHeaders = WebSocketHandshake.lowerCaseHeaders(request);
    }

    @Override
    public void run() {
        int closeCode = CLOSE_ABNORMAL;
        String closeReason = null;
        boolean abnormal = false;
        try {
            String key = handshakeHeaders.get(HttpHeader.HttpHeaderNames.SEC_WEBSOCKET_KEY.getName().toLowerCase(Locale.ROOT));
            String protocol = handshakeHeaders.get(HttpHeader.HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL.getName().toLowerCase(Locale.ROOT));
            String response = WebSocketHandshake.buildUpgradeResponse(key, protocol, configuration.getServerMessage());
            writeHandshake(response);

            String path = request.getUrl();
            session = new WebSocketSessionImpl(channel, path, handshakeHeaders, configuration.getConnectionTimeout());
            LOGGER.debug(() -> "WebSocket opened, session: " + session.getId() + ", path: " + path);
            endpoint.onOpen(session);

            closeCode = readLoop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            abnormal = true;
            LOGGER.debug("WebSocket connection interrupted, session: " + (session == null ? "?" : session.getId()));
        } catch (Exception e) {
            abnormal = true;
            if (session != null) {
                endpoint.onError(session, e);
            }
            LOGGER.error("WebSocket connection error", e);
        } finally {
            if (session != null) {
                if (abnormal) {
                    session.closeQuietly(CLOSE_ABNORMAL, null);
                    closeCode = CLOSE_ABNORMAL;
                    closeReason = null;
                } else {
                    // normal path: the close handshake already completed or the
                    // channel was released by the loop, make sure nothing leaks
                    session.closeQuietly(closeCode, closeReason);
                }
                try {
                    endpoint.onClose(session, closeCode, closeReason);
                } catch (Exception e) {
                    LOGGER.error("WebSocket endpoint onClose error", e);
                }
            } else {
                closeChannel();
            }
        }
    }

    /**
     * Reads and dispatches frames until the connection ends. Side effect:
     * updates {@link #closeCode}/{@link #closeReason} fields when a close
     * frame terminates the loop.
     *
     * @return the close status code observed, {@link #CLOSE_ABNORMAL} when the
     *         peer (or an idle timeout) dropped the connection without a close handshake
     */
    private int readLoop() throws IOException, InterruptedException {
        long readTimeout = configuration.getWebsocketReadTimeout();
        WebSocketOpcode messageOpcode = null;
        ByteArrayOutputStream fragments = new ByteArrayOutputStream();

        while (true) {
            WebSocketFrame frame = WebSocketCodec.readFrame(channel, readTimeout);
            if (frame == null) {
                // clean end of stream or idle timeout without a close handshake
                break;
            }
            WebSocketOpcode opcode = frame.getOpcode();
            switch (opcode) {
                case CLOSE -> {
                    int statusCode = parseCloseCode(frame.getPayload());
                    String reason = parseCloseReason(frame.getPayload());
                    // the close handshake is complete once the echo has been sent,
                    // the loop only has to terminate
                    session.close(statusCode, reason);
                    closeCode = statusCode;
                    closeReason = reason;
                    return statusCode;
                }
                case PING -> session.sendPong(frame.getPayload());
                case PONG -> endpoint.onPong(session, frame.getPayload());
                case CONTINUATION -> {
                    if (messageOpcode == null) {
                        protocolError("continuation frame without a starting frame");
                        return CLOSE_PROTOCOL_ERROR;
                    }
                    fragments.write(frame.getPayload());
                    if (frame.isFin()) {
                        dispatchMessage(messageOpcode, fragments.toByteArray());
                        messageOpcode = null;
                        fragments.reset();
                    }
                }
                case TEXT, BINARY -> {
                    if (messageOpcode != null) {
                        protocolError("new message frame while a fragmented message is open");
                        return CLOSE_PROTOCOL_ERROR;
                    }
                    if (frame.isFin()) {
                        dispatchMessage(opcode, frame.getPayload());
                    } else {
                        messageOpcode = opcode;
                        fragments.write(frame.getPayload());
                    }
                }
            }
        }
        return CLOSE_ABNORMAL;
    }

    private void dispatchMessage(WebSocketOpcode opcode, byte[] payload) {
        if (opcode == WebSocketOpcode.TEXT) {
            endpoint.onMessage(session, new String(payload, configuration.getCharset()));
        } else {
            endpoint.onMessage(session, payload);
        }
    }

    private void protocolError(String message) throws IOException {
        LOGGER.warn("WebSocket protocol violation: " + message + ", session: " + session.getId());
        session.close(CLOSE_PROTOCOL_ERROR, message);
    }

    private static int parseCloseCode(byte[] payload) {
        if (payload.length >= 2) {
            return ((payload[0] & 0xFF) << 8) | (payload[1] & 0xFF);
        }
        return 1005;
    }

    private static String parseCloseReason(byte[] payload) {
        if (payload.length > 2) {
            return new String(payload, 2, payload.length - 2, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Writes the raw {@code 101} response. Deliberately does not go through
     * {@link WebSocketSessionImpl} because the session does not exist yet.
     */
    private void writeHandshake(String response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.US_ASCII));
        try {
            long timeout = configuration.getConnectionTimeout();
            while (buffer.hasRemaining()) {
                var future = channel.write(buffer);
                if (timeout > 0) {
                    future.get(timeout, TimeUnit.MILLISECONDS);
                } else {
                    future.get();
                }
            }
        } catch (TimeoutException e) {
            throw new IOException("WebSocket handshake write timed out", e);
        } catch (java.util.concurrent.ExecutionException e) {
            throw new IOException("WebSocket handshake write failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("WebSocket handshake write interrupted", e);
        }
    }

    private void closeChannel() {
        try {
            channel.close();
        } catch (IOException e) {
            LOGGER.error("WebSocket channel close error", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);
}
