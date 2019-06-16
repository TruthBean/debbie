package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.servlet.request.ServletRouterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class StaticResourcesServlet extends HttpServlet {
    private final ServletConfiguration configuration;
    private final BeanFactoryHandler handler;

    public StaticResourcesServlet(ServletConfiguration configuration, BeanFactoryHandler handler) {
        this.configuration = configuration;
        this.handler = handler;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var requestAdapter = new ServletRouterRequest(req);
        byte[] bytes = MvcRouterHandler.handleStaticResources(requestAdapter, configuration.getStaticResourcesMapping());
        if (bytes != null) {
            resp.setContentLength(bytes.length);
            try (var outputStream = resp.getOutputStream()) {
                outputStream.write(bytes);
            } catch (IOException e) {
                LOGGER.error(" ", e);
            }
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(StaticResourcesServlet.class);
}
