package com.truthbean.debbie.undertow.handler;

import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.undertow.UndertowRouterRequest;
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

    private final AttachmentKey<UndertowRouterRequest> request = AttachmentKey.create(UndertowRouterRequest.class);

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
        String url = exchange.getRequestURI();
        List<Pattern> urlPattern = filterInfo.getUrlPattern();
        RouterFilter routerFilter = filterInfo.getRouterFilter();
        for (Pattern pattern : urlPattern) {
            if (pattern.matcher(url).find()) {
                if (routerFilter.doFilter(routerRequest)) {
                    Map<String, Object> attributes = routerRequest.getAttributes();
                    if (attributes!=null && !attributes.isEmpty()) {
                        exchange.putAttachment(request, routerRequest);
                    }
                    if (next.getClass() == DispatcherHttpHandler.class) {
                        ((DispatcherHttpHandler) next).setRequest(request);
                    }
                    next.handleRequest(exchange);
                }
            }
        }

    }
}
