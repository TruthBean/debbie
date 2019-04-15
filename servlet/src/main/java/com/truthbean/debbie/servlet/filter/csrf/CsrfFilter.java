package com.truthbean.debbie.servlet.filter.csrf;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 14:15
 */
public class CsrfFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            chain.doFilter(request, response);
    }
}
