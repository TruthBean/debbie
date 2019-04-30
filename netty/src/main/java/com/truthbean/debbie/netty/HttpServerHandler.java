package com.truthbean.debbie.netty;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.net.uri.UriUtils;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterInvokeResult;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DefaultRouterRequest routerRequest;
    private final NettyConfiguration configuration;

    public HttpServerHandler(NettyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2);
        LOGGER.debug(msg.getClass().getName());
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            io.netty.handler.codec.http.HttpRequest httpRequest = (io.netty.handler.codec.http.HttpRequest) msg;
            io.netty.handler.codec.http.HttpMethod httpMethod = httpRequest.method();

            String uri = httpRequest.uri();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            Map<String, List<String>> queries = queryStringDecoder.parameters();
            uri = UriUtils.uri(uri);

            this.routerRequest = new DefaultRouterRequest();
            this.routerRequest.setMethod(com.truthbean.debbie.mvc.request.HttpMethod.valueOf(httpMethod.name()));
            this.routerRequest.setUrl(uri);

            Map<String, List<String>> headers = new HashMap<>();
            httpRequest.headers().names().forEach(name -> headers.put(name, httpRequest.headers().getAll(name)));
            this.routerRequest.setHeaders(headers);
            this.routerRequest.setResponseType(MediaType.APPLICATION_JSON);
            this.routerRequest.setParameters(new HashMap<>());
            this.routerRequest.setQueries(queries);

            if (HttpUtil.is100ContinueExpected(httpRequest)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            keepAlive = HttpUtil.isKeepAlive(httpRequest);
        }
        if (msg instanceof LastHttpContent) {
            LastHttpContent httpContent = (LastHttpContent) msg;

            ByteBuf content = httpContent.content();
            RouterInfo routerInfo = MvcRouterHandler.getMatchedRouter(routerRequest, configuration.getDefaultTypes());
            MvcRouterHandler.handleRouter(routerInfo);
            RouterInvokeResult invokeResult = routerInfo.getResponse();
            MediaType responseType = invokeResult.getResponseType();
            Object resp = invokeResult.getData();

            ByteBuf byteBuf = Unpooled.wrappedBuffer("null".getBytes());
            if (resp instanceof String) {
                byteBuf = Unpooled.wrappedBuffer(((String) resp).getBytes());
            } else {
                if (resp instanceof byte[]) {
                    byteBuf = Unpooled.wrappedBuffer((byte[]) resp);
                }
            }
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
            response.headers().set(CONTENT_TYPE, responseType.getValue());
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

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);
}