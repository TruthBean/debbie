/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.filter;

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.servlet.request.ServletRouterRequest;
import com.truthbean.debbie.servlet.response.ServletResponseHandler;
import com.truthbean.debbie.servlet.response.ServletRouterResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterFilterWrapper extends HttpFilter implements RouterFilter {

    private RouterFilter filter;
    private Class<? extends RouterFilter> filterType;

    private final MediaTypeInfo defaultResponseType;
    private BeanFactoryContext applicationContext;

    public RouterFilterWrapper(Class<? extends RouterFilter> filterType, BeanFactoryContext applicationContext,
                               MediaTypeInfo defaultResponseType) {
        this.filterType = filterType;
        this.applicationContext = applicationContext;
        this.defaultResponseType = defaultResponseType;
    }

    public RouterFilterWrapper(RouterFilter filter, MediaTypeInfo defaultResponseType) {
        this.filter = filter;
        this.defaultResponseType = defaultResponseType;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        return this.filter.preRouter(request, response);
    }

    @Override
    public Boolean postRouter(RouterRequest request, RouterResponse response) {
        return this.filter.postRouter(request, response);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (filter == null) {
            synchronized (this) {
                if (filter == null) {
                    filter = applicationContext.factory(filterType);
                }
            }
        }
        ServletRouterRequest routerRequest = new ServletRouterRequest(request);
        ServletRouterResponse routerResponse = new ServletRouterResponse(response);
        boolean doFilter = false;
        if (this.preRouter(routerRequest, routerResponse)) {
            Map<String, Object> attributes = routerRequest.getAttributes();
            if (attributes != null && !attributes.isEmpty()) {
                attributes.forEach(request::setAttribute);
            }
            doFilter = true;
        }
        Boolean post = this.postRouter(routerRequest, routerResponse);
        if (post != null) {
            var handler = new ServletResponseHandler(routerRequest.getHttpServletRequest(), routerResponse.getResponse());
            if (post) {
                if (routerResponse.getStatus() == null) {
                    // 默认请求成功
                    routerResponse.setStatus(HttpStatus.OK);
                }
                handler.changeResponseWithoutContent(routerResponse);
                handler.handle(routerResponse, defaultResponseType, false);
                doFilter = false;
            } else {
                handler.changeResponseWithoutContent(routerResponse);
                doFilter = true;
            }
        }
        if (doFilter) {
            chain.doFilter(request, response);
        }
    }
}
