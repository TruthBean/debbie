/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.netty;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.FileNameUtils;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.filter.RouterFilterHandler;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.core.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
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
    private final ThreadLocal<NettyRouterRequest> routerRequest;
    private final MvcConfiguration mvcConfiguration;
    private final NettyConfiguration configuration;

    private final SessionManager sessionManager;

    private final ApplicationContext applicationContext;

    public HttpServerHandler(NettyConfiguration configuration, MvcConfiguration mvcConfiguration,
                             SessionManager sessionManager, ApplicationContext applicationContext) {
        this.configuration = configuration;
        this.mvcConfiguration = mvcConfiguration;
        this.sessionManager = sessionManager;
        this.applicationContext = applicationContext;
        this.routerRequest = new ThreadLocal<>();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2);
        LOGGER.debug(() -> msg.getClass().getName());

        if (msg instanceof HttpRequest) {
            LOGGER.debug("msg instanceof HttpRequest. ");
            HttpRequest httpRequest = (HttpRequest) msg;

            var nettyRouterRequest = new NettyRouterRequest(sessionManager, httpRequest, configuration.getHost(), configuration.getPort());
            routerRequest.set(nettyRouterRequest);

            if (HttpUtil.is100ContinueExpected(httpRequest)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            keepAlive = HttpUtil.isKeepAlive(httpRequest);
        }
        if (msg instanceof FullHttpMessage) {
            LOGGER.debug("msg instanceof FullHttpMessage");
        }
        if (msg instanceof LastHttpContent) {
            LOGGER.debug("msg instanceof LastHttpContent. ");

            LastHttpContent httpContent = (LastHttpContent) msg;

            NettyRouterRequest routerRequest = this.routerRequest.get();
            if (routerRequest != null) {
                var contentType = routerRequest.getContentType();
                if (MediaType.APPLICATION_FORM_URLENCODED.isSame(contentType)
                        || contentType.toMediaType() == MediaType.MULTIPART_FORM_DATA) {
                    routerRequest.handleHttpData(httpContent);
                }
                if (!MediaType.APPLICATION_FORM_URLENCODED.isSame(contentType)
                        && contentType.toMediaType() != MediaType.MULTIPART_FORM_DATA) {

                    routerRequest.setInputStreamBody(httpContent);

                    routerRequest.setTextBody(httpContent);

                    HttpHeaders trailer = httpContent.trailingHeaders();
                    routerRequest.setParameters(trailer);
                }

                handleRouter(ctx);

                routerRequest.resetHttpRequest();
                httpContent.release();
                this.routerRequest.remove();
            }
        }
    }

    private void handleRouter(ChannelHandlerContext ctx) {
        NettyRouterRequest routerRequest = this.routerRequest.get();
        if (routerRequest != null) {
            final RouterResponse routerResponse = new RouterResponse();
            if (handleFilter(routerRequest, routerResponse, ctx)) {
                byte[] bytes = MvcRouterHandler.handleStaticResources(routerRequest, mvcConfiguration.getStaticResourcesMapping());
                if (bytes != null) {
                    ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
                    HttpStatus httpStatus = routerResponse.getStatus();
                    httpStatus = httpStatus != null ? httpStatus : HttpStatus.OK;
                    HttpResponseStatus status = HttpResponseStatus.valueOf(httpStatus.getStatus());
                    if (routerResponse.getResponseType() == null) {
                        MediaTypeInfo responseType = routerRequest.getResponseType();
                        if (responseType != null) {
                            routerResponse.setResponseType(responseType);
                        } else {
                            String url = routerRequest.getUrl();
                            String extension = FileNameUtils.getExtension(url);
                            if (StringUtils.hasText(extension)) {
                                MediaType type = MediaType.getTypeByUriExt(extension);
                                if (type != MediaType.ANY)
                                    routerResponse.setResponseType(type);
                            }
                        }
                    }
                    doHandleResponse(ctx, routerRequest, routerResponse, byteBuf, status, false);
                } else {
                    RouterInfo routerInfo = MvcRouterHandler.getMatchedRouter(routerRequest, mvcConfiguration);
                    RouterResponse response = routerInfo.getResponse();
                    response.copyNoNull(routerResponse);
                    var afterResponse = MvcRouterHandler.handleRouter(routerInfo, applicationContext);
                    doResponse(routerRequest, afterResponse, ctx);
                }
            } else {
                doResponse(routerRequest, routerResponse, ctx);
            }
        }

    }

    private void doHandleResponse(ChannelHandlerContext ctx, NettyRouterRequest routerRequest, RouterResponse routerResponse,
                                  ByteBuf byteBuf, HttpResponseStatus status, boolean setContentType) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, byteBuf);

        handleResponseWithoutContent(response, routerRequest, routerResponse, setContentType);

        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }

    /**
     * return true, go router
     * return false, doFilter
     *
     * @param request routerRequest
     * @param response routerResponse
     * @param ctx ChannelHandlerContext
     * @return boolean
     */
    private boolean handleFilter(RouterRequest request, RouterResponse response, ChannelHandlerContext ctx) {
        // reverse order to fix the chain order
        Set<RouterFilterInfo> filters = RouterFilterManager.getFilters();
        for (RouterFilterInfo filterInfo : filters) {
            var filter = new RouterFilterHandler(filterInfo, applicationContext);
            var filterType = filterInfo.getRouterFilterType();
            if (!filter.notFilter(request)) {
                if (filter.preRouter(request, response)) {
                    LOGGER.trace(() -> filterType + " no pre filter");
                } else {
                    Boolean post = filter.postRouter(request, response);
                    if (post != null) {
                        LOGGER.trace(() -> filterType + " post filter");
                        if (post) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void beforeResponse(RouterRequest request, RouterResponse response) {
        // reverse order to fix the chain order
        Set<RouterFilterInfo> filters = RouterFilterManager.getFilters();
        for (RouterFilterInfo filterInfo : filters) {
            var filter = new RouterFilterHandler(filterInfo, applicationContext);
            Boolean router = filter.postRouter(request, response);
            if (router != null && router) {
                break;
            }
        }
    }

    private void handleResponseWithoutContent(FullHttpResponse response,
                                              NettyRouterRequest routerRequest, RouterResponse routerResponse,
                                              boolean setContentType) {
        RouterSession session = routerRequest.getSession();
        if (session != null) {
            response.headers().set("JSESSIONID", session.getId());
        }
        Set<HttpCookie> cookies = routerResponse.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            List<String> cookieStrList = new ArrayList<>();
            for (HttpCookie cookie : cookies) {
                cookieStrList.add(ServerCookieEncoder.LAX.encode(transform(cookie)));
            }
            response.headers().set(SET_COOKIE, cookieStrList);
        }

        Map<String, String> headers = routerResponse.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(response.headers()::set);
        }

        MediaTypeInfo responseType = routerResponse.getResponseType();
        if (responseType == null) {
            responseType = mvcConfiguration.getDefaultContentType();
        }

        if (setContentType)
            response.headers().set(CONTENT_TYPE, responseType.toString());
    }

    private Cookie transform(HttpCookie cookie) {
        Cookie result = new DefaultCookie(cookie.getName(), cookie.getValue());
        result.setDomain(cookie.getDomain());
        result.setHttpOnly(cookie.isHttpOnly());
        result.setMaxAge(cookie.getMaxAge());
        result.setPath(cookie.getPath());
        result.setSecure(cookie.getSecure());
        result.setWrap(true);
        return result;
    }

    private void doResponse(NettyRouterRequest routerRequest, RouterResponse routerResponse, ChannelHandlerContext ctx) {
        if (routerRequest == null) {
            return;
        }

        Object resp = routerResponse.getContent();

        ByteBuf byteBuf = Unpooled.wrappedBuffer("null".getBytes());
        boolean setContentType = true;
        if (resp instanceof String) {
            byteBuf = Unpooled.wrappedBuffer(((String) resp).getBytes());
        } else {
            if (resp instanceof byte[]) {
                setContentType = false;
                byteBuf = Unpooled.wrappedBuffer((byte[]) resp);
            }
        }

        HttpStatus httpStatus = routerResponse.getStatus();
        if (httpStatus == null) {
            httpStatus = HttpStatus.OK;
        }
        HttpResponseStatus status = HttpResponseStatus.valueOf(httpStatus.getStatus());
        doHandleResponse(ctx, routerRequest, routerResponse, byteBuf, status, setContentType);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
        // clear ThreadLocal
        routerRequest.remove();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);
}