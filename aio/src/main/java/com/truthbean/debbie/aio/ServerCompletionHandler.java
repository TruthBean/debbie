/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.core.concurrent.ThreadLoggerUncaughtExceptionHandler;
import com.truthbean.debbie.aio.websocket.WebSocketEndpoint;
import com.truthbean.debbie.aio.websocket.WebSocketEndpointManager;
import com.truthbean.debbie.aio.websocket.WebSocketHandler;
import com.truthbean.debbie.aio.websocket.WebSocketHandshake;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-20 16:10
 */
class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    /**
     * WebSocket connections stay open for a long time, so their frame loop is
     * offloaded from the AIO channel-group threads onto a dedicated pool;
     * plain HTTP requests keep being handled inline.
     */
    private static final ExecutorService WEBSOCKET_EXECUTOR = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new NamedThreadFactory("aio-websocket-").setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler()));

    private final ApplicationContext applicationContext;
    private final AioServerConfiguration configuration;
    private final MvcConfiguration mvcConfiguration;
    private final SessionManager sessionManager;
    private final AsynchronousServerSocketChannel server;

    ServerCompletionHandler(AioServerConfiguration configuration, MvcConfiguration mvcConfiguration,
                            SessionManager sessionManager, final ApplicationContext applicationContext,
                            AsynchronousServerSocketChannel server) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.mvcConfiguration = mvcConfiguration;
        this.sessionManager = sessionManager;
        this.server = server;
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel attachment) {
        long begin = System.currentTimeMillis();
        // handle this connection
        handleRequest(channel, (routerRequest, rawLines) -> handleResponse(routerRequest, rawLines, channel, attachment), begin);
        // handle next connection
        if (attachment != null) {
            attachment.accept(attachment, ServerCompletionHandler.this);
        }
    }

    private void handleRequest(AsynchronousSocketChannel channel, BiConsumer<RouterRequest, List<String>> routerRequestConsumer,
                               long beginTime) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("remote address: " + channel.getRemoteAddress().toString());
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        //获取客户端发送的请求
        var requestCompleteHandler = RequestCompleteHandler.getInstance();
        requestCompleteHandler.handle(configuration.getConnectionTimeout(), configuration.isIgnoreEncode(), channel,
                this.sessionManager, routerRequestConsumer, beginTime);
    }

    private void handleResponse(RouterRequest routerRequest, List<String> rawLines, AsynchronousSocketChannel channel,
                                AsynchronousServerSocketChannel attachment) {
        if (routerRequest == null) {
            return;
        }
        if (configuration.isWebsocketEnable() && WebSocketEndpointManager.hasEndpoints()) {
            // the websocket upgrade shares this server's http port: sniff the
            // raw request lines before the regular mvc dispatch
            if (WebSocketHandshake.isWebSocketUpgrade(rawLines)) {
                WebSocketEndpoint endpoint = WebSocketEndpointManager.matchEndpoint(routerRequest.getUrl());
                if (endpoint != null) {
                    LOGGER.debug(() -> "WebSocket upgrade on " + routerRequest.getUrl());
                    // never run the long-lived frame loop on the accept chain
                    WEBSOCKET_EXECUTOR.execute(new WebSocketHandler(channel, routerRequest, endpoint, configuration));
                    return;
                }
                rejectUpgrade(channel);
                return;
            }
        }
        var responseCompletionHandler = new ResponseCompletionHandler(applicationContext, routerRequest, configuration, mvcConfiguration);
        responseCompletionHandler.handle(channel, () -> {
            try {
                channel.shutdownOutput();
                channel.shutdownInput();
            } catch (IOException e) {
                LOGGER.error("", e);
            }

            try {
                channel.close();
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        });
    }

    /**
     * Writes a plain 400 answer for an upgrade request that carries the
     * WebSocket handshake but matches no registered endpoint.
     */
    private void rejectUpgrade(AsynchronousSocketChannel channel) {
        byte[] body = "Invalid websocket request\n".getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/" + configuration.getHttpVersion() + " 400 Bad Request\r\n"
                + "Server: " + configuration.getServerMessage() + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "\r\n";
        ByteBuffer buffer = ByteBuffer.allocate(response.length() + body.length);
        buffer.put(response.getBytes(StandardCharsets.US_ASCII));
        buffer.put(body);
        buffer.flip();
        try {
            channel.write(buffer).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("", e);
        } catch (java.util.concurrent.ExecutionException e) {
            LOGGER.error("", e);
        }
        try {
            channel.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
        LOGGER.error("", exc);
        try {
            attachment.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(ServerCompletionHandler.class);
}
