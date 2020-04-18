package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.servlet.request.ServletRouterRequest;
import com.truthbean.debbie.servlet.response.ServletResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final BeanFactoryHandler handler;

    public DispatcherServlet(ServletConfiguration configuration, BeanFactoryHandler handler) {
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

        var requestAdapter = new ServletRouterRequest(req);

        byte[] bytes = MvcRouterHandler.handleStaticResources(requestAdapter, configuration.getStaticResourcesMapping());
        if (bytes != null) {
            resp.setContentLength(bytes.length);
            try (var outputStream = resp.getOutputStream()) {
                outputStream.write(bytes);
            } catch (IOException e) {
                LOGGER.error(" ", e);
            }
        } else {
            var routerInfo = MvcRouterHandler.getMatchedRouter(requestAdapter, configuration);
            LOGGER.debug("routerInfo invoke method params : " + routerInfo.getMethodParams());
            RouterResponse response = MvcRouterHandler.handleRouter(routerInfo, handler);

            // handle response
            ServletResponseHandler handler = new ServletResponseHandler(req, resp);
            handler.changeResponseWithoutContent(response);
            handler.handle(response, routerInfo.getDefaultResponseType());
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);
}
