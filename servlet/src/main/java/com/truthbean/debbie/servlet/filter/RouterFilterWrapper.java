package com.truthbean.debbie.servlet.filter;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.servlet.request.ServletRouterRequest;
import com.truthbean.debbie.servlet.response.ServletResponseHandler;
import com.truthbean.debbie.servlet.response.ServletRouterResponse;

import javax.servlet.*;
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

    private BeanFactoryHandler beanFactoryHandler;

    public RouterFilterWrapper(Class<? extends RouterFilter> filterType, BeanFactoryHandler beanFactoryHandler) {
        this.filterType = filterType;
        this.beanFactoryHandler = beanFactoryHandler;
    }

    public RouterFilterWrapper(RouterFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        return this.filter.preRouter(request, response);
    }

    @Override
    public void postRouter(RouterRequest request, RouterResponse response) {
        this.filter.postRouter(request, response);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (filter == null) {
            synchronized (this) {
                if (filter == null) {
                    filter = beanFactoryHandler.factory(filterType);
                }
            }
        }
        ServletRouterRequest routerRequest = new ServletRouterRequest(request);
        ServletRouterResponse routerResponse = new ServletRouterResponse(response);
        if (this.preRouter(routerRequest, routerResponse)) {
            Map<String, Object> attributes = routerRequest.getAttributes();
            if (attributes != null && !attributes.isEmpty()) {
                attributes.forEach(request::setAttribute);
            }
            chain.doFilter(request, response);
        } else {
            this.postRouter(routerRequest, routerResponse);
            var handler = new ServletResponseHandler(routerRequest.getHttpServletRequest(), routerResponse.getResponse());
            if (routerResponse.getStatus() == null) {
                // 默认请求成功
                routerResponse.setStatus(HttpStatus.OK);
            }
            handler.handle(routerResponse);
        }
    }
}
