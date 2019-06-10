package com.truthbean.debbie.netty;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.request.filter.RouterFilterHandler;
import com.truthbean.debbie.mvc.request.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.request.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.netty.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter { // (1)

    private boolean keepAlive;
    private volatile NettyRouterRequest routerRequest;
    private final NettyConfiguration configuration;

    private SessionManager sessionManager;

    private BeanFactoryHandler beanFactoryHandler;

    public HttpServerHandler(NettyConfiguration configuration, SessionManager sessionManager, BeanFactoryHandler beanFactoryHandler) {
        this.configuration = configuration;
        this.sessionManager = sessionManager;
        this.beanFactoryHandler = beanFactoryHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2);
        LOGGER.debug(msg.getClass().getName());

        if (msg instanceof HttpRequest) {
            LOGGER.debug("msg instanceof HttpRequest. ");
            HttpRequest httpRequest = (HttpRequest) msg;

            routerRequest = new NettyRouterRequest(sessionManager, httpRequest, configuration.getHost(), configuration.getPort());

            if (HttpUtil.is100ContinueExpected(httpRequest)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            keepAlive = HttpUtil.isKeepAlive(httpRequest);
        }
        if (msg instanceof LastHttpContent) {
            LOGGER.debug("msg instanceof LastHttpContent. ");

            LastHttpContent httpContent = (LastHttpContent) msg;
            assert routerRequest != null;
            var contentType = routerRequest.getContentType();
            if (MediaType.APPLICATION_FORM_URLENCODED.isSame(contentType)
                    || contentType.toMediaType() == MediaType.MULTIPART_FORM_DATA) {
                routerRequest.handleHttpData(httpContent);
            }
            if (!MediaType.APPLICATION_FORM_URLENCODED.isSame(contentType)
                    && contentType.toMediaType() != MediaType.MULTIPART_FORM_DATA) {

                ByteBuf content = httpContent.content();
                routerRequest.setInputStreamBody(content);

                routerRequest.setTextBody(httpContent);

                HttpHeaders trailer = httpContent.trailingHeaders();
                routerRequest.setParameters(trailer);
            }

            handleRouter(ctx);

            routerRequest.resetHttpRequest();
            httpContent.release();
            routerRequest = null;
        }
    }

    private void handleRouter(ChannelHandlerContext ctx) {
        byte[] bytes = MvcRouterHandler.handleStaticResources(routerRequest, configuration);
        if (bytes != null) {
            RouterResponse routerResponse = new RouterResponse();
            if (handleFilter(routerRequest, routerResponse, ctx)) {
                ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);

                RouterSession session = routerRequest.getSession();
                if (session != null) {
                    response.headers().add(COOKIE, session.getId());
                }

                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                if (!keepAlive) {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.writeAndFlush(response);
                }
            }
        } else {
            RouterInfo routerInfo = MvcRouterHandler.getMatchedRouter(routerRequest, configuration);
            RouterResponse routerResponse = routerInfo.getResponse();
            if (handleFilter(routerRequest, routerResponse, ctx)) {
                MvcRouterHandler.handleRouter(routerInfo, beanFactoryHandler);
                routerResponse = routerInfo.getResponse();
                doResponse(routerResponse, ctx);
            }
        }
    }

    private boolean handleFilter(RouterRequest request, RouterResponse response, ChannelHandlerContext ctx) {
        // reverse order to fix the chain order
        List<RouterFilterInfo> filters = RouterFilterManager.getReverseOrderFilters();
        for (RouterFilterInfo filterInfo : filters) {
            var filter = new RouterFilterHandler(filterInfo, beanFactoryHandler);
            var result = filter.preRouter(request, response);
            if (result != null && !result) {
                doResponse(response, ctx);
                return false;
            }
        }
        return true;
    }

    private void beforeResponse(RouterRequest request, RouterResponse response) {
        // reverse order to fix the chain order
        List<RouterFilterInfo> filters = RouterFilterManager.getReverseOrderFilters();
        for (RouterFilterInfo filterInfo : filters) {
            var filter = new RouterFilterHandler(filterInfo, beanFactoryHandler);
            filter.postRouter(request, response);
        }
    }

    private void doResponse(RouterResponse routerResponse, ChannelHandlerContext ctx) {
        beforeResponse(routerRequest, routerResponse);

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

        RouterSession session = routerRequest.getSession();
        if (session != null) {
            response.headers().add(COOKIE, session.getId());
        }

        response.headers().set(CONTENT_TYPE, responseType.toString());
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(response);
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