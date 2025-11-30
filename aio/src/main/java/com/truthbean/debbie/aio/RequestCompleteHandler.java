/**
 * Copyright (c) 2025 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.server.session.SessionManager;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:13
 */
class RequestCompleteHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RequestCompleteHandler.class);

    // 单例模式
    private static final RequestCompleteHandler INSTANCE = new RequestCompleteHandler();

    private RequestCompleteHandler() {
    }

    static RequestCompleteHandler getInstance() {
        return INSTANCE;
    }

    void handle(final long connectionTimeout, final boolean ignoreEncode,
                final AsynchronousSocketChannel channel, final SessionManager sessionManager,
                final Consumer<RouterRequest> routerRequestConsumer, final long beginTime) {
        try {
            // 请求内容
            StringBuilder stringBuilder = new StringBuilder();

            // ByteBuffer是非线程安全的，如果要在多个线程间共享同一个ByteBuffer，需要考虑线程安全性问题
            try {
                readRequestSync(connectionTimeout, channel, stringBuilder);
            } catch (Exception e) {
                LOG.error("Read request failed. ", e);
                closeChannel(channel);
                return;
            }

            SocketAddress remoteAddress = channel.getRemoteAddress();

            var request = stringBuilder.toString();
            LOG.debug("client message: " + request);
            List<String> lines = request.lines().collect(Collectors.toList());
            if (lines.size() > 1) {
                routerRequestConsumer.accept(new RawRequestWrapper(lines, remoteAddress, sessionManager, ignoreEncode));
                long end = System.currentTimeMillis();
                LOG.debug("Request processed in {} ms", end - beginTime);
            }
        } catch (Exception e) {
            LOG.error("Read failed. ", e);
            closeChannel(channel);
        }
    }

    private void readRequest(long connectionTimeout, AsynchronousSocketChannel channel, StringBuilder stringBuilder) {
        final int bufferSize = 8192;
        var byteBuffer = ByteBuffer.allocateDirect(bufferSize);
        channel.read(byteBuffer, connectionTimeout, TimeUnit.MILLISECONDS, byteBuffer, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result <= 0) {
                    return;
                }
                // 重置 position和mark
                byteBuffer.flip();
                var remaining = byteBuffer.remaining();
                var reqBytes = new byte[remaining];
                byteBuffer.get(reqBytes);
                byteBuffer.clear();

                var part = new String(reqBytes);
                stringBuilder.append(part);

                if (result < bufferSize) {
                    return;
                }
                try {
                    // 继续读，防止请求内容被截断
                    readRequest(connectionTimeout, channel, stringBuilder);
                } catch (Exception e) {
                    LOG.error("Read next request part failed. ", e);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                LOG.error("Read failed. ", exc);
                closeChannel(channel);
            }
        });
    }

    private void readRequestSync(long connectionTimeout, AsynchronousSocketChannel channel, StringBuilder stringBuilder) {
        final int bufferSize = 8192;
        try {
            // 请求内容

            // ByteBuffer是非线程安全的，如果要在多个线程间共享同一个ByteBuffer，需要考虑线程安全性问题
            var byteBuffer = ByteBuffer.allocateDirect(bufferSize);
            while (true) {
                var read = channel.read(byteBuffer);
                Integer size;
                try {
                    size = connectionTimeout <= 0 ? read.get() : read.get(connectionTimeout, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    size = 0;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("remote client message is null, it could be a options request.", e);
                    } else {
                        LOG.debug("remote client message is null, it could be a options request.");
                    }
                }
                if (size <= 0) {
                    break;
                }

                // 重置 position和mark
                byteBuffer.flip();
                var remaining = byteBuffer.remaining();
                var reqBytes = new byte[remaining];
                byteBuffer.get(reqBytes);
                byteBuffer.clear();

                var part = new String(reqBytes);
                stringBuilder.append(part);

                if (size < bufferSize) {
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("", e);
        }
    }

    private void closeChannel(AsynchronousSocketChannel channel) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                LOG.debug("Channel closed: " + channel);
            }
        } catch (IOException e) {
            LOG.error("Channel closed error. ", e);
        }
    }
}
