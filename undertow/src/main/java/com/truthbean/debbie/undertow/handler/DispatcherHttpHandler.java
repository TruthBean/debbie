package com.truthbean.debbie.undertow.handler;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.undertow.UndertowConfiguration;
import com.truthbean.debbie.undertow.UndertowResponseHandler;
import com.truthbean.debbie.undertow.UndertowRouterRequest;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
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

    private final UndertowConfiguration configuration;
    private final BeanFactoryHandler beanFactoryHandler;

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
        return httpRequest;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        RouterRequest httpRequest = getHttpRequestInfo(exchange);
        byte[] bytes = MvcRouterHandler.handleStaticResources(httpRequest, configuration.getStaticResourcesMapping());
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
            var responseHandler = new UndertowResponseHandler(exchange);
            responseHandler.changeResponseWithoutContent(response);
            responseHandler.handle(response, routerInfo.getDefaultResponseType());
        }
    }
}
