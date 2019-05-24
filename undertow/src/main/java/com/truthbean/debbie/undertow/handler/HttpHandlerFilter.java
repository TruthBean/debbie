package com.truthbean.debbie.undertow.handler;

import com.truthbean.debbie.core.bean.BeanFactory;
import com.truthbean.debbie.mvc.request.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.filter.RouterFilterInfo;
import com.truthbean.debbie.undertow.UndertowResponseHandler;
import com.truthbean.debbie.undertow.UndertowRouterRequest;
import com.truthbean.debbie.undertow.UndertowRouterResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpHandlerFilter implements HttpHandler {

    private final HttpHandler next;
    private final RouterFilterInfo filterInfo;

    public HttpHandlerFilter(final HttpHandler next, RouterFilterInfo filterInfo) {
        this.next = next;
        this.filterInfo = filterInfo;
    }

    private static final AttachmentKey<UndertowRouterRequest> request = AttachmentKey.create(UndertowRouterRequest.class);

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if(exchange.isInIoThread()) {
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
        RouterFilter routerFilter = BeanFactory.factory(routerFilterType);

        boolean filter = false;

        for (String s : rawUrlPattern) {
            if (s.equals(url)) {
                filter = true;
                if (routerFilter.doFilter(routerRequest, routerResponse)) {
                    if (next.getClass() == DispatcherHttpHandler.class) {
                        ((DispatcherHttpHandler) next).setRequest(request);
                        next.handleRequest(exchange);
                    } else {
                        next.handleRequest(exchange);
                    }
                } else {
                    UndertowResponseHandler handler = new UndertowResponseHandler(exchange);
                    handler.handle(routerResponse);
                    return;
                }
                break;
            }
        }

        List<Pattern> urlPattern = filterInfo.getUrlPattern();
        for (Pattern pattern : urlPattern) {
            if (pattern.matcher(url).find()) {
                filter = true;
                if (routerFilter.doFilter(routerRequest, routerResponse)) {
                    if (next.getClass() == DispatcherHttpHandler.class) {
                        ((DispatcherHttpHandler) next).setRequest(request);
                        next.handleRequest(exchange);
                    } else {
                        next.handleRequest(exchange);
                    }
                } else {
                    UndertowResponseHandler handler = new UndertowResponseHandler(exchange);
                    handler.handle(routerResponse);
                    return;
                }
                break;
            }
        }

        if (!filter) {
            if (next.getClass() == DispatcherHttpHandler.class) {
                ((DispatcherHttpHandler) next).setRequest(request);
                next.handleRequest(exchange);
            } else {
                next.handleRequest(exchange);
            }
        }
    }
}
