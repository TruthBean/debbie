package com.truthbean.debbie.servlet.response;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.util.StringUtils;
import com.truthbean.debbie.mvc.response.ResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.servlet.ServletRouterCookie;
import com.truthbean.debbie.servlet.response.view.JspView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ServletResponseHandler implements ResponseHandler {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public ServletResponseHandler(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void handle(RouterResponse routerResponse) {
        var any = routerResponse.getContent();
        var headers = routerResponse.getHeaders();
        if (!headers.isEmpty()) {
            headers.forEach((key, value) -> response.setHeader(key, value));
        }
        var cookies = routerResponse.getCookies();
        if (!cookies.isEmpty()) {
            cookies.forEach(cookie -> response.addCookie(new ServletRouterCookie(cookie).getCookie()));
        }
        if (any == null) {
            LOGGER.debug("response is null");
        } else if (AbstractTemplateView.isTemplateView(any)) {
            var jspView = new JspView();
            if (any instanceof AbstractTemplateView) {
                jspView.from((AbstractTemplateView) any);
            }
            jspView.setHttpServletRequest(request);
            jspView.setHttpServletResponse(response);
            if (!StringUtils.isBlank(routerResponse.getTemplatePrefix())) {
                jspView.setPrefix(routerResponse.getTemplatePrefix());
            }
            if (!StringUtils.isBlank(routerResponse.getTemplateSuffix())) {
                jspView.setSuffix(routerResponse.getTemplateSuffix());
            }
            jspView.transfer();
        } else if (any instanceof byte[]) {
            // response.reset();
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getValue());
            response.setContentLength(((byte[]) any).length);
            try (var outputStream = response.getOutputStream()) {
                outputStream.write((byte[]) any);
            } catch (IOException e) {
                LOGGER.error(" ", e);
            }
        } else {
            try {
                // response.reset();
                response.setContentType(routerResponse.getResponseType().toString());
                response.getWriter().println(any);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletResponseHandler.class);
}
