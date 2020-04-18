package com.truthbean.debbie.undertow.handler;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.undertow.UndertowConfiguration;
import com.truthbean.debbie.undertow.UndertowResponseHandler;
import com.truthbean.debbie.undertow.UndertowRouterRequest;
import com.truthbean.debbie.undertow.UndertowRouterResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpHandlerFilter implements HttpHandler {

    private final HttpHandler next;
    private final RouterFilterInfo filterInfo;

    private final BeanFactoryHandler beanFactoryHandler;
    private final UndertowConfiguration configuration;

    public HttpHandlerFilter(final HttpHandler next, RouterFilterInfo filterInfo, BeanFactoryHandler beanFactoryHandler,
                             UndertowConfiguration configuration) {
        this.next = next;
        this.filterInfo = filterInfo;
        this.beanFactoryHandler = beanFactoryHandler;
        this.configuration = configuration;
    }

    private static final AttachmentKey<UndertowRouterRequest> request = AttachmentKey.create(UndertowRouterRequest.class);

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        UndertowRouterRequest routerRequest = exchange.getAttachment(request);
        if (routerRequest == null) {
            routerRequest = new UndertowRouterRequest(exchange);
        }
        exchange.putAttachment(request, routerRequest);

        UndertowRouterResponse routerResponse = new UndertowRouterResponse();

        String url = exchange.getRequestURI();
        List<String> rawUrlPattern = filterInfo.getRawUrlPattern();

        Class<? extends RouterFilter> routerFilterType = filterInfo.getRouterFilterType();
        RouterFilter routerFilter = filterInfo.getFilterInstance();
        if (routerFilter == null) {
            routerFilter = beanFactoryHandler.factory(routerFilterType);
        }

        if (routerFilter.notFilter(routerRequest)) {
            logger.trace(routerFilterType + " do not filter");
            handleServerExchangeRequest(exchange);
            return;
        }

        boolean filter = false;

        for (String s : rawUrlPattern) {
            if (s.equals(url)) {
                filter = true;
                if (handleFilter(routerFilter, routerRequest, routerResponse, exchange)) {
                    return;
                }
                break;
            }
        }

        List<Pattern> urlPattern = filterInfo.getUrlPattern();
        for (Pattern pattern : urlPattern) {
            if (pattern.matcher(url).find()) {
                filter = true;
                if (handleFilter(routerFilter, routerRequest, routerResponse, exchange)) {
                    return;
                }
                break;
            }
        }

        if (!filter) {
            handleServerExchangeRequest(exchange);
        }
    }

    private void handleServerExchangeRequest(final HttpServerExchange exchange) throws Exception {
        if (next.getClass() == DispatcherHttpHandler.class) {
            ((DispatcherHttpHandler) next).setRequest(request);
            next.handleRequest(exchange);
        } else {
            next.handleRequest(exchange);
        }
    }

    private boolean handleFilter(final RouterFilter routerFilter,
                                 final UndertowRouterRequest routerRequest, final UndertowRouterResponse routerResponse,
                                 final HttpServerExchange exchange) throws Exception {
        var filter = false;
        Class<? extends RouterFilter> filterClass = routerFilter.getClass();
        if (routerFilter.preRouter(routerRequest, routerResponse)) {
            logger.trace(filterClass + " no pre filter");
            if (next.getClass() != DispatcherHttpHandler.class) {
                next.handleRequest(exchange);
            }
        } else {
            filter = true;
            logger.trace(filterClass + " pre filter");
        }
        Boolean post = routerFilter.postRouter(routerRequest, routerResponse);
        if (filter) {
            if (post != null) {
                logger.trace(filterClass + " post filter");
                UndertowResponseHandler handler = new UndertowResponseHandler(exchange);
                if (post) {
                    handler.changeResponseWithoutContent(routerResponse);
                    handler.handle(routerResponse, configuration.getDefaultContentType());
                    return true;
                } else {
                    handler.changeResponseWithoutContent(routerResponse);
                }
            }
            return false;
        } else if (next.getClass() == DispatcherHttpHandler.class) {
            logger.trace(filterClass + " to dispatcherHttpHandler");
            ((DispatcherHttpHandler) next).setRequest(request);
            next.handleRequest(exchange);
            return true;
        }

        return filter;
    }

    private static final Logger logger = LoggerFactory.getLogger(HttpHandlerFilter.class);
}
