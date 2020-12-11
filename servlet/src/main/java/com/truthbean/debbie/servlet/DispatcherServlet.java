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
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.servlet.request.HttpServletRequestWrapper;
import com.truthbean.debbie.servlet.response.ServletResponseHandler;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/25 22:06.
 */
public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = -8253171030384092538L;
    private final ServletConfiguration configuration;
    private final ApplicationContext handler;

    public DispatcherServlet(ServletConfiguration configuration, ApplicationContext handler) {
        this.configuration = configuration;
        this.handler = handler;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatcher(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatcher(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doHead(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatcher(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatcher(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatcher(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO do cache
        super.service(req, resp);
    }

    private void dispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.debug("dispatcher servlet .... ");

        HttpServletRequestWrapper requestWrapper;
        if (req instanceof HttpServletRequestWrapper) {
            requestWrapper = (HttpServletRequestWrapper) req;
        } else {
            requestWrapper = new HttpServletRequestWrapper(req);
        }
        var routerRequest = requestWrapper.getRouterRequest();

        byte[] bytes = MvcRouterHandler.handleStaticResources(routerRequest, configuration.getStaticResourcesMapping());
        if (bytes != null) {
            resp.setContentLength(bytes.length);
            try (var outputStream = resp.getOutputStream()) {
                outputStream.write(bytes);
            } catch (IOException e) {
                LOGGER.error(" ", e);
            }
        } else {
            var routerInfo = MvcRouterHandler.getMatchedRouter(routerRequest, configuration);
            RouterResponse response = MvcRouterHandler.handleRouter(routerInfo, handler);

            // handle response
            ServletResponseHandler handler = new ServletResponseHandler(req, resp);
            handler.changeResponseWithoutContent(response);
            handler.handle(response, routerInfo.getDefaultResponseType());
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);
}
