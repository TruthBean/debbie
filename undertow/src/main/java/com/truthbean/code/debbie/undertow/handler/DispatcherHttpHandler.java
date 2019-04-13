package com.truthbean.code.debbie.undertow.handler;

import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.code.debbie.mvc.router.RouterInfo;
import com.truthbean.code.debbie.undertow.UndertowConfiguration;
import com.truthbean.code.debbie.undertow.UndertowRouterRequest;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherHttpHandler.class);

    private UndertowConfiguration configuration;

    public DispatcherHttpHandler(final UndertowConfiguration configuration) {
        this.configuration = configuration;
    }

    private RouterRequest getHttpRequestInfo(final HttpServerExchange exchange) {
        RouterRequest httpRequest = new UndertowRouterRequest(exchange);

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
        RouterInfo routerInfo = MvcRouterHandler.getMatchedRouter(httpRequest, configuration.getDefaultTypes());
        LOGGER.debug(httpRequest.toString());

        if (!exchange.isResponseChannelAvailable()) {
            return;
        }

        MvcRouterHandler.handleRouter(routerInfo);

        var response = routerInfo.getResponse();
        Object responseData = response.getData();

        var sender = exchange.getResponseSender();
        if (responseData instanceof ByteBuffer) {
            sender.send((ByteBuffer) responseData);
        }
        if (responseData instanceof byte[]) {
            sender.send(ByteBuffer.wrap((byte[]) responseData));
        } else {
            // 404
            // Response Headers
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, response.getResponseType().getValue());
            // Response Sender
            if (responseData != null) {
                sender.send(responseData.toString());
            } else {
                sender.send("");
            }
        }
    }
}
