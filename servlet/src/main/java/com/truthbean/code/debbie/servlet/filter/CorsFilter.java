package com.truthbean.code.debbie.servlet.filter;

import com.truthbean.code.debbie.servlet.ServletConfiguration;
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
 * Created on 2019/3/25 22:03.
 */
public class CorsFilter extends HttpFilter {

    private static final long serialVersionUID = -5174032877157965619L;
    private ServletConfiguration configuration;

    public CorsFilter(ServletConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (configuration.isCors()) {
            LOGGER.debug("filter cors");
            var cors = configuration.getCors();
            if (cors != null && !cors.isEmpty()) {
                cors.forEach(response::setHeader);
            } else {
                response.setHeader("Access-Control-Allow-Origin", "*");

                response.setHeader("Access-Control-Allow-Headers", "User-Agent,Origin,Cache-Control,Content-type,Date,Server,withCredentials,AccessToken");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
                response.setHeader("Access-Control-Max-Age", "1209600");
                response.setHeader("Access-Control-Expose-Headers", "accesstoken");
                response.setHeader("Access-Control-Request-Headers", "accesstoken");
                response.setHeader("Expires", "-1");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("pragma", "no-cache");
            }
        }

        chain.doFilter(request, response);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);
}
