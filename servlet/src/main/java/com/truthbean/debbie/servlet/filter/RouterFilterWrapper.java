package com.truthbean.debbie.servlet.filter;

import com.truthbean.debbie.core.bean.BeanFactory;
import com.truthbean.debbie.mvc.request.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
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

    public RouterFilterWrapper(Class<? extends RouterFilter> filterType) {
        this.filterType = filterType;
    }

    @Override
    public boolean doFilter(RouterRequest request, RouterResponse response) {
        return this.filter.doFilter(request, response);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (filter == null) {
            synchronized (this) {
                if (filter == null) {
                    filter = BeanFactory.factory(filterType);
                }
            }
        }
        ServletRouterRequest routerRequest = new ServletRouterRequest(request);
        ServletRouterResponse routerResponse = new ServletRouterResponse(response);
        if (this.doFilter(routerRequest, routerResponse)) {
            Map<String, Object> attributes = routerRequest.getAttributes();
            if (attributes!=null && !attributes.isEmpty()) {
                attributes.forEach(request::setAttribute);
            }
            chain.doFilter(request, response);
        } else {
            ServletResponseHandler handler = new ServletResponseHandler(request, response);
            handler.handle(routerResponse);
        }
    }
}
