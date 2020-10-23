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

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.undertow.UndertowConfiguration;
import com.truthbean.debbie.undertow.UndertowResponseHandler;
import com.truthbean.debbie.undertow.UndertowRouterRequest;
import com.truthbean.debbie.undertow.UndertowRouterResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpHandlerFilter implements HttpHandler {

    private final HttpHandler next;
    private final RouterFilterInfo filterInfo;

    private final ApplicationContext applicationContext;
    private final UndertowConfiguration configuration;

    public HttpHandlerFilter(final HttpHandler next, RouterFilterInfo filterInfo, ApplicationContext applicationContext,
                             UndertowConfiguration configuration) {
        this.next = next;
        this.filterInfo = filterInfo;
        this.applicationContext = applicationContext;
        this.configuration = configuration;
    }

    private static final AttachmentKey<UndertowRouterRequest> request = AttachmentKey.create(UndertowRouterRequest.class);

    public Class<? extends RouterFilter> getFilterType() {
        return filterInfo.getRouterFilterType();
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        if (!exchange.isResponseChannelAvailable()) {
            return;
        }
        UndertowRouterRequest routerRequest = exchange.getAttachment(request);
        if (routerRequest == null) {
            routerRequest = new UndertowRouterRequest(exchange);
        }
        exchange.putAttachment(request, routerRequest);

        UndertowRouterResponse routerResponse = routerRequest.getRouterResponse();
        if (routerResponse == null) {
            routerResponse = new UndertowRouterResponse();
            routerRequest.setRouterResponse(routerResponse);
        }

        String url = exchange.getRequestURI();
        List<String> rawUrlPattern = filterInfo.getRawUrlPattern();

        Class<? extends RouterFilter> routerFilterType = filterInfo.getRouterFilterType();
        RouterFilter routerFilter = filterInfo.getFilterInstance();
        if (routerFilter == null) {
            GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
            routerFilter = globalBeanFactory.factory(routerFilterType);
        }

        if (routerFilter.notFilter(routerRequest)) {
            logger.trace(routerFilterType + " do not filter");
            handleServerExchangeRequest(exchange);
            return;
        }

        boolean filter = false;

        for (String s : rawUrlPattern) {
            if (s.equals(url)) {
                filter = handleFilter(routerFilter, routerFilterType, routerRequest, routerResponse, exchange);
                if (filter) {
                    return;
                }
                break;
            }
        }

        List<Pattern> urlPattern = filterInfo.getUrlPattern();
        for (Pattern pattern : urlPattern) {
            if (pattern.matcher(url).find()) {
                filter = handleFilter(routerFilter, routerFilterType, routerRequest, routerResponse, exchange);
                if (filter) {
                    return;
                }
                break;
            }
        }

        handleServerExchangeRequest(exchange);
    }

    private void handleServerExchangeRequest(final HttpServerExchange exchange) throws Exception {
        if (next.getClass() == DispatcherHttpHandler.class) {
            logger.trace("next is DispatcherHttpHandler");
            ((DispatcherHttpHandler) next).setRequest(request);
        }
        next.handleRequest(exchange);
    }

    private boolean handleFilter(final RouterFilter routerFilter, final Class<? extends RouterFilter> filterClass,
                                 final UndertowRouterRequest routerRequest, final UndertowRouterResponse routerResponse,
                                 final HttpServerExchange exchange) throws Exception {
        var filter = false;
        Class<? extends HttpHandler> nextClass = next.getClass();
        if (nextClass == HttpHandlerFilter.class) {
            logger.trace("next handler: " + ((HttpHandlerFilter) next).getFilterType());
        } else
            logger.trace("next handler: " + nextClass.getName());
        if (routerFilter.preRouter(routerRequest, routerResponse)) {
            logger.trace(filterClass + " no pre filter");
            if (nextClass != DispatcherHttpHandler.class) {
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
                handler.changeResponseWithoutContent(routerResponse);
                if (post) {
                    handler.handle(routerResponse, configuration.getDefaultContentType());
                    return true;
                }
            }
            return false;
        } else if (nextClass == DispatcherHttpHandler.class) {
            logger.trace(filterClass + " to dispatcherHttpHandler");
            ((DispatcherHttpHandler) next).setRequest(request);
            next.handleRequest(exchange);
            return true;
        }

        return false;
    }

    private static final Logger logger = LoggerFactory.getLogger(HttpHandlerFilter.class);
}
