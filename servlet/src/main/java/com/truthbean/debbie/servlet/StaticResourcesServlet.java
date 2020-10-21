/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.servlet.request.HttpServletRequestWrapper;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class StaticResourcesServlet extends HttpServlet {
    private final ServletConfiguration configuration;
    private final ApplicationContext applicationContext;

    public StaticResourcesServlet(ServletConfiguration configuration, ApplicationContext applicationContext) {
        this.configuration = configuration;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletRequestWrapper requestWrapper;
        if (req instanceof HttpServletRequestWrapper) {
            requestWrapper = (HttpServletRequestWrapper) req;
        } else {
            requestWrapper = new HttpServletRequestWrapper(req);
        }
        byte[] bytes = MvcRouterHandler.handleStaticResources(requestWrapper.getRouterRequest(), configuration.getStaticResourcesMapping());
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
