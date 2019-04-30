package com.truthbean.debbie.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 15:41
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    private NettyConfiguration configuration;
    public HttpChannelInitializer(NettyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new HttpServerCodec())
                .addLast(new HttpServerHandler(this.configuration));
    }
}