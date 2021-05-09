/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-20 16:10
 */
class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private final ApplicationContext applicationContext;
    private final AioServerConfiguration configuration;
    private final SessionManager sessionManager;

    ServerCompletionHandler(AioServerConfiguration configuration, SessionManager sessionManager,
                                   final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.sessionManager = sessionManager;
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel attachment) {
        /*if (attachment != null && !attachment.isOpen()) {
            // accept the next connection
            listener.accept(attachment, this);
        } else {
            listener.accept(null, this);
        }*/

        // handle this connection
        RouterRequest routerRequest = handleRequest(channel);
        handleResponse(routerRequest, channel, attachment);
    }

    private RouterRequest handleRequest(AsynchronousSocketChannel channel) {
        try {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("remote address: " + channel.getRemoteAddress().toString());
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        //获取客户端发送的请求
        var requestCompleteHandler = new RequestCompleteHandler();
        return requestCompleteHandler.handle(channel, this.sessionManager);
    }

    private void handleResponse(RouterRequest routerRequest, AsynchronousSocketChannel channel,
                                             AsynchronousServerSocketChannel attachment) {
        if (routerRequest != null) {
            var responseCompletionHandler = new ResponseCompletionHandler(applicationContext, routerRequest, configuration);
            responseCompletionHandler.handle(channel);
        }

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

        if (attachment != null)
            attachment.accept(attachment, this);
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
