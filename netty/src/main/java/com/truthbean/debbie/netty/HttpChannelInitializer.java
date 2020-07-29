/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.netty;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.server.session.SessionManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 15:41
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpServerHandler httpServerHandler;

    public HttpChannelInitializer(NettyConfiguration configuration, SessionManager sessionManager,
                                  ApplicationContext applicationContext) {
        this.httpServerHandler = new HttpServerHandler(configuration, sessionManager, applicationContext);
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(new HttpServerCodec())
            .addLast(new HttpObjectAggregator(1024 * 1024))
            .addLast(new HttpServerExpectContinueHandler())
            .addLast(httpServerHandler);
    }
}