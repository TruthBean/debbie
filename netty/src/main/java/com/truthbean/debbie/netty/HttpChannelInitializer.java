package com.truthbean.debbie.netty;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.netty.session.SessionManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 15:41
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpServerHandler httpServerHandler;

    public HttpChannelInitializer(NettyConfiguration configuration, SessionManager sessionManager,
                                  BeanFactoryHandler beanFactoryHandler) {
        this.httpServerHandler = new HttpServerHandler(configuration, sessionManager, beanFactoryHandler);
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new HttpServerCodec())
                .addLast(httpServerHandler);
    }
}