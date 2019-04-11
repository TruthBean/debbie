package com.truthbean.code.debbie.servlet;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.util.StringUtils;
import com.truthbean.code.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.code.debbie.mvc.response.view.AbstractView;
import com.truthbean.code.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.code.debbie.servlet.request.ServletRouterRequest;
import com.truthbean.code.debbie.servlet.response.view.JspView;
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
    private ServletConfiguration configuration;

    public DispatcherServlet(ServletConfiguration configuration) {
        this.configuration = configuration;
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

        var handler = new MvcRouterHandler();
        var routerInfo = handler.getMatchedRouter(requestAdapter, configuration.getDefaultTypes());
        LOGGER.debug("routerInfo invoke method params : " + routerInfo.getMethodParams());
        handler.handleRouter(routerInfo);
        var any = routerInfo.getResponse().getData();

        if (any == null) {
            LOGGER.debug("response is null");
        } else if (isTemplateView(any)) {
            var jspView = new JspView();
            if (any instanceof AbstractTemplateView) {
                jspView.from((AbstractTemplateView) any);
            }
            jspView.setHttpServletRequest(req);
            jspView.setHttpServletResponse(resp);
            if (!StringUtils.isBlank(routerInfo.getTemplatePrefix())) {
                jspView.setPrefix(routerInfo.getTemplatePrefix());
            }
            if (!StringUtils.isBlank(routerInfo.getTemplateSuffix())) {
                jspView.setSuffix(routerInfo.getTemplateSuffix());
            }
            jspView.transfer();
        } else if (any instanceof byte[]) {
            resp.reset();
            resp.setContentType(MediaType.APPLICATION_OCTET_STREAM.getValue());
            resp.setContentLength(((byte[]) any).length);
            try (var outputStream = resp.getOutputStream()) {
                outputStream.write((byte[]) any);
            }
        } else {
            LOGGER.debug(any.toString());
            resp.reset();
            resp.setContentType(routerInfo.getResponse().getResponseType().getValue());
            resp.getWriter().println(any);
        }
    }

    private boolean isTemplateView(Object any){
        return any instanceof AbstractTemplateView || any instanceof AbstractView;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);
}
