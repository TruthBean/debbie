package com.truthbean.debbie.netty;

import com.truthbean.debbie.core.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handles a server-side channel.
 *
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter { // (1)

    private boolean keepAlive;
    private NettyRouterRequest routerRequest;
    private final NettyConfiguration configuration;

    private HttpPostRequestDecoder decoder;
    private static final HttpDataFactory nettyHttpDataFactory = new DefaultHttpDataFactory();

    public HttpServerHandler(NettyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2);
        LOGGER.debug(msg.getClass().getName());
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            io.netty.handler.codec.http.HttpRequest httpRequest = (io.netty.handler.codec.http.HttpRequest) msg;

            routerRequest = new NettyRouterRequest(httpRequest, configuration.getHost(), configuration.getPort());
            decoder = new HttpPostRequestDecoder(nettyHttpDataFactory, httpRequest);

            if (HttpUtil.is100ContinueExpected(httpRequest)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            keepAlive = HttpUtil.isKeepAlive(httpRequest);
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            assert routerRequest != null;
            routerRequest.setInputStreamBody(content);

            decoder.offer(httpContent);
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    try {
                        writeHttpData(data);
                    } finally {
                        data.release();
                    }
                }
            }
        }
        if (msg instanceof LastHttpContent) {
            LastHttpContent httpContent = (LastHttpContent) msg;

            /*decoder.offer(httpContent);
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    try {
                        writeHttpData(data);
                    } finally {
                        data.release();
                    }
                }
            }*/

            HttpHeaders trailer = httpContent.trailingHeaders();
            assert routerRequest != null;
            routerRequest.setParameters(trailer);

            RouterInfo routerInfo = MvcRouterHandler.getMatchedRouter(routerRequest, configuration);
            MvcRouterHandler.handleRouter(routerInfo);
            RouterResponse routerResponse = routerInfo.getResponse();
            MediaTypeInfo responseType = routerResponse.getResponseType();
            Object resp = routerResponse.getContent();

            ByteBuf byteBuf = Unpooled.wrappedBuffer("null".getBytes());
            if (resp instanceof String) {
                byteBuf = Unpooled.wrappedBuffer(((String) resp).getBytes());
            } else {
                if (resp instanceof byte[]) {
                    byteBuf = Unpooled.wrappedBuffer((byte[]) resp);
                }
            }
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
            response.headers().set(CONTENT_TYPE, responseType.toString());
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            if (!keepAlive) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private void writeHttpData(InterfaceHttpData data) {
        /**
         * HttpDataType有三种类型
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
            } catch (IOException e1) {
                e1.printStackTrace();
                LOGGER.error("BODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " Error while reading value: " + e1.getMessage());
                return;
            }
            if (value.length() > 100) {
                LOGGER.error("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " data too long");
            } else {
                LOGGER.debug("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.toString() + "\r\n");
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);
}