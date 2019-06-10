package com.truthbean.debbie.undertow.handler;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.undertow.UndertowConfiguration;
import com.truthbean.debbie.undertow.UndertowRouterRequest;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DispatcherHttpHandler implements HttpHandler {

    private AttachmentKey<UndertowRouterRequest> request;
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherHttpHandler.class);

    private UndertowConfiguration configuration;
    private BeanFactoryHandler beanFactoryHandler;

    public DispatcherHttpHandler(final UndertowConfiguration configuration, BeanFactoryHandler beanFactoryHandler) {
        this.configuration = configuration;
        this.beanFactoryHandler = beanFactoryHandler;
    }

    public void setRequest(AttachmentKey<UndertowRouterRequest> request) {
        this.request = request;
    }

    private RouterRequest getHttpRequestInfo(final HttpServerExchange exchange) {
        UndertowRouterRequest httpRequest = exchange.getAttachment(request);
        if (httpRequest == null) {
            httpRequest = new UndertowRouterRequest(exchange);
        }

        HeaderMap responseHeaders = exchange.getResponseHeaders();
        var cors = this.configuration.getCors();
        if (cors != null) {
            cors.forEach((k, v) -> responseHeaders.add(new HttpString(k), v));
        }

        return httpRequest;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        RouterRequest httpRequest = getHttpRequestInfo(exchange);
        byte[] bytes = MvcRouterHandler.handleStaticResources(httpRequest, configuration);
        if (bytes != null) {
            var sender = exchange.getResponseSender();
            sender.send(ByteBuffer.wrap(bytes));
        } else {
            RouterInfo routerInfo = MvcRouterHandler.getMatchedRouter(httpRequest, configuration);
            LOGGER.debug(httpRequest.toString());

            if (!exchange.isResponseChannelAvailable()) {
                return;
            }

            MvcRouterHandler.handleRouter(routerInfo, beanFactoryHandler);

            var response = routerInfo.getResponse();
            Object responseData = response.getContent();

            var sender = exchange.getResponseSender();
            if (responseData instanceof ByteBuffer) {
                sender.send((ByteBuffer) responseData);
            }
            if (responseData instanceof byte[]) {
                sender.send(ByteBuffer.wrap((byte[]) responseData));
            } else {
                // 404
                // Response Headers
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, response.getResponseType().toString());
                // Response Sender
                if (responseData != null) {
                    sender.send(responseData.toString());
                } else {
                    sender.send("");
                }
            }
        }
    }
}
