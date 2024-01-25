/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.filter.RouterFilterHandler;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.core.util.OsUtils;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:19
 */
class ResponseCompletionHandler {

    private final ApplicationContext applicationContext;
    private final MvcConfiguration mvcConfiguration;
    private final AioServerConfiguration configuration;
    private final RouterRequest routerRequest;

    ResponseCompletionHandler(ApplicationContext applicationContext, RouterRequest routerRequest,
                              AioServerConfiguration configuration, MvcConfiguration mvcConfiguration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.mvcConfiguration = mvcConfiguration;
        this.routerRequest = routerRequest;
    }

    private void writeChannel(AsynchronousSocketChannel channel, RouterResponse routerResponse, Object result) {
        logger.trace("response content: " + result);
        var lf = OsUtils.getLf();
        // 响应头的参数
        var serverLine = ("Server: " + configuration.getServerMessage() + lf);
        var statusLine = "HTTP/" + configuration.getHttpVersion() + " 200 OK" + lf;
        MediaTypeInfo responseType = routerResponse.getResponseType();
        if (responseType == null) {
            responseType = mvcConfiguration.getDefaultContentType();
        }
        var contentTypeLine = HttpHeader.HttpHeaderNames.CONTENT_TYPE.getName() + ": " + responseType.toString() + lf;

        var contentLength = 1024;
        if (result instanceof String) {
            // 中文编码问题，不能直接通过String的length()方法获取
            contentLength = ((String) result).getBytes().length;
        } else if (result instanceof byte[]) {
            contentLength = ((byte[]) result).length;
        }
        var contentLengthLine = HttpHeader.HttpHeaderNames.CONTENT_LENGTH.getName() + ": " + contentLength + lf;
        var resultBuilder = new StringBuilder(statusLine)
                .append(serverLine);
        handleResponseWithoutContent(resultBuilder, routerResponse, lf);
        var resultStr = resultBuilder.append(contentTypeLine)
                .append(contentLengthLine)
                .append(lf)
                .append(result)
                .toString();

        logger.trace("response: " + resultStr);

        Future<Integer> future;
        if (result instanceof byte[]) {
            byte[] bytes = (byte[]) result;
            //先把头部转换成byte[]
            var headerBuilder = new StringBuilder(statusLine)
                    .append(serverLine);
            handleResponseWithoutContent(headerBuilder, routerResponse, lf);
            var header = headerBuilder.append(contentTypeLine)
                    .append(contentLengthLine)
                    .append(lf).toString();

            var headerByteArray = header.getBytes();
            //然后合并
            byte[] merge = new byte[headerByteArray.length + ((byte[]) result).length];
            System.arraycopy(headerByteArray, 0, merge, 0, headerByteArray.length);
            System.arraycopy(bytes, 0, merge, headerByteArray.length, bytes.length);
            future = channel.write(ByteBuffer.wrap(merge));
        } else {
            future = channel.write(ByteBuffer.wrap(resultStr.getBytes()));
        }

        try {
            Integer integer = future.get();
            logger.trace("future get: " + integer);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("", e);
        }

    }

    private void handleResponseWithoutContent(StringBuilder sb, RouterResponse routerResponse, String lf) {
        RouterSession session = routerRequest.getSession();
        if (session != null) {
            sb.append("JSESSIONID: ").append(session.getId()).append(lf);
        }
        /*Set<HttpCookie> cookies = routerResponse.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            List<String> cookieStrList = new ArrayList<>();
            for (HttpCookie cookie : cookies) {
                cookieStrList.add(ServerCookieEncoder.LAX.encode(transform(cookie)));
            }
            response.headers().set(SET_COOKIE, cookieStrList);
        }*/

        Map<String, String> headers = routerResponse.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append(lf));
        }
    }

    void handle(AsynchronousSocketChannel channel) {
        logger.trace("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        logger.trace(routerRequest.toString());
        logger.trace("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        final RouterResponse routerResponse = new RouterResponse();
        if (handleFilter(routerRequest, routerResponse)) {
            // static
            byte[] bytes = MvcRouterHandler.handleStaticResources(routerRequest, mvcConfiguration.getStaticResourcesMapping());
            if (bytes != null) {
                writeChannel(channel, routerResponse, bytes);
            } else {
                RouterInfo matchedRouter = MvcRouterHandler.getMatchedRouter(routerRequest, mvcConfiguration);
                logger.debug("--> handle response");
                matchedRouter.getResponse().copyNoNull(routerResponse);
                var afterResponse = MvcRouterHandler.handleRouter(matchedRouter, this.applicationContext);
                MediaTypeInfo responseType = afterResponse.getResponseType();
                if (responseType == null) {
                    responseType = matchedRouter.getDefaultResponseType();
                }
                afterResponse.setResponseType(responseType);
                writeChannel(channel, afterResponse, afterResponse.getContent());
            }
        } else {
            writeChannel(channel, routerResponse, routerResponse.getContent());
        }
    }

    /**
     * return true, go router
     * return false, doFilter
     *
     * @param request router request
     * @param response raw response
     * @return boolean
     */
    private boolean handleFilter(RouterRequest request, RouterResponse response) {
        // reverse order to fix the chain order
        Set<RouterFilterInfo> filters = RouterFilterManager.getFilters();
        for (RouterFilterInfo filterInfo : filters) {
            var filter = new RouterFilterHandler(filterInfo, applicationContext);
            var filterType = filterInfo.getRouterFilterType();
            if (!filter.notFilter(request)) {
                if (filter.preRouter(request, response)) {
                    logger.trace(() -> filterType + " no pre filter");
                } else {
                    Boolean post = filter.postRouter(request, response);
                    if (post != null) {
                        logger.trace(() -> filterType + " post filter");
                        if (post) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static final Logger logger = LoggerFactory.getLogger(ResponseCompletionHandler.class);
}
