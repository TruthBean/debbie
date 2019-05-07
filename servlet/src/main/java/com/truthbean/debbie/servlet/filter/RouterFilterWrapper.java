package com.truthbean.debbie.servlet.filter;

import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.servlet.request.ServletRouterRequest;

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

    public RouterFilterWrapper(RouterFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean doFilter(RouterRequest request) {
        return this.filter.doFilter(request);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletRouterRequest routerRequest = new ServletRouterRequest(request);
        if (this.doFilter(routerRequest)) {
            Map<String, Object> attributes = routerRequest.getAttributes();
            if (attributes!=null && !attributes.isEmpty()) {
                attributes.forEach(request::setAttribute);
            }
            chain.doFilter(request, response);
        }

    }
}
