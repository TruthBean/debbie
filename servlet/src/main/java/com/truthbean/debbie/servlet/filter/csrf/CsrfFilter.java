package com.truthbean.debbie.servlet.filter.csrf;

import com.truthbean.debbie.servlet.ServletConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ServletConfiguration configuration;

    public CsrfFilter(ServletConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String csrfTokenInHeader = request.getHeader("CSRF-TOKEN");
        logger.debug("crsfToken in header: " + csrfTokenInHeader);
        String csrfTokenInParams = request.getParameter("_CSRF_TOKEN");
        logger.debug("crsfToken in hidden form: " + csrfTokenInParams);
        chain.doFilter(request, response);
    }

    private final Logger logger = LoggerFactory.getLogger(CsrfFilter.class);
}
