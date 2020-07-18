/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.undertow.handler;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.undertow.UndertowConfiguration;
import com.truthbean.debbie.undertow.UndertowResponseHandler;
import com.truthbean.debbie.undertow.UndertowRouterRequest;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DispatcherHttpHandler implements HttpHandler {

    private AttachmentKey<UndertowRouterRequest> request;
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherHttpHandler.class);

    private final UndertowConfiguration configuration;
    private final DebbieApplicationContext applicationContext;

    public DispatcherHttpHandler(final UndertowConfiguration configuration, DebbieApplicationContext applicationContext) {
        this.configuration = configuration;
        this.applicationContext = applicationContext;
    }

    public void setRequest(AttachmentKey<UndertowRouterRequest> request) {
        this.request = request;
    }

    private RouterRequest getHttpRequestInfo(final HttpServerExchange exchange) {
        UndertowRouterRequest httpRequest = exchange.getAttachment(request);
        if (httpRequest == null) {
            httpRequest = new UndertowRouterRequest(exchange);
        }

        // HeaderMap responseHeaders = exchange.getResponseHeaders();
        return httpRequest;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        if (!exchange.isResponseChannelAvailable()) {
            return;
        }
        RouterRequest httpRequest = getHttpRequestInfo(exchange);
        byte[] bytes = MvcRouterHandler.handleStaticResources(httpRequest, configuration.getStaticResourcesMapping());
        if (bytes != null) {
            var sender = exchange.getResponseSender();
            sender.send(ByteBuffer.wrap(bytes));
        } else {
            RouterInfo routerInfo = MvcRouterHandler.getMatchedRouter(httpRequest, configuration);
            LOGGER.debug(httpRequest::toString);

            MvcRouterHandler.handleRouter(routerInfo, applicationContext);

            var response = routerInfo.getResponse();
            var responseHandler = new UndertowResponseHandler(exchange);
            responseHandler.changeResponseWithoutContent(response);
            responseHandler.handle(response, routerInfo.getDefaultResponseType());
        }
    }
}
