package com.truthbean.code.debbie.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/15 21:21.
 */
public class CharacterEncodingFilter extends HttpFilter {

    private static final long serialVersionUID = 2547926167686086400L;

    private static final String UTF8 = "UTF-8";

    private String charset = UTF8;

    @Override
    public void init(FilterConfig config) throws ServletException {
        String charset = config.getInitParameter("charset");
        if (charset != null) {
            this.charset = charset.trim();
        }
        super.init(config);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LOGGER.debug("character encoding filter");
        request.setCharacterEncoding(this.charset);
        response.setCharacterEncoding(this.charset);
        chain.doFilter(request, response);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterEncodingFilter.class);
}