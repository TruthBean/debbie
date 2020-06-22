/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.response;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.ResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.servlet.ServletRouterCookie;
import com.truthbean.debbie.servlet.response.view.JspView;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ServletResponseHandler implements ResponseHandler {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public ServletResponseHandler(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void changeResponseWithoutContent(RouterResponse routerResponse) {
        var headers = routerResponse.getHeaders();
        if (!headers.isEmpty()) {
            headers.forEach((key, value) -> response.setHeader(key, value));
        }
        var cookies = routerResponse.getCookies();
        if (!cookies.isEmpty()) {
            cookies.forEach(cookie -> response.addCookie(new ServletRouterCookie(cookie).getCookie()));
        }
        var responseType = routerResponse.getResponseType();
        if (responseType != null) {
            response.setContentType(responseType.toString());
        }
        try {
            Map<String, Object> modelAttributes = routerResponse.getModelAttributes();
            if (modelAttributes != null && !modelAttributes.isEmpty()) {
                modelAttributes.forEach((key, value) -> {
                    request.setAttribute(key, value);
                });
            }
        } catch (Exception e) {
            LOGGER.error("request.setAttribute error", e);
        }
    }

    public void copyResponseWithoutContent() {
        // head
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        if (headerNames != null && !headerNames.isEmpty()) {
            for (String name : headerNames) {
                String header = response.getHeader(name);
                headers.put(name, header);
            }
        }
        // status
        int status = response.getStatus();
        // trailerFields
        Supplier<Map<String, String>> trailerFields = response.getTrailerFields();
        // characterEncoding
        String characterEncoding = response.getCharacterEncoding();
        // contentType
        String contentType = response.getContentType();
        // locale
        Locale locale = response.getLocale();

        response.reset();

        if (!headers.isEmpty()) {
            headers.forEach(response::addHeader);
        }
        response.setStatus(status);
        if (trailerFields != null)
            response.setTrailerFields(trailerFields);
        response.setCharacterEncoding(characterEncoding);
        response.setContentType(contentType);
        response.setLocale(locale);
    }

    @Override
    public void handle(RouterResponse routerResponse, MediaTypeInfo defaultResponseType) {
        handle(routerResponse, defaultResponseType, true);
    }

    public void handle(RouterResponse routerResponse, MediaTypeInfo defaultResponseType, boolean reset) {
        HttpStatus status = routerResponse.getStatus();
        response.setStatus(status.getStatus());

        var any = routerResponse.getContent();
        var responseType = routerResponse.getResponseType();

        if (any == null) {
            LOGGER.debug(() -> "response is null");
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
        } else {
            if (reset)
                copyResponseWithoutContent();
            setResponse(routerResponse, defaultResponseType);
        }
    }

    public void setResponse(RouterResponse routerResponse, MediaTypeInfo defaultResponseType) {
        var status = routerResponse.getStatus();
        var any = routerResponse.getContent();
        var responseType = routerResponse.getResponseType();

        if (any instanceof byte[]) {
            response.setStatus(routerResponse.getStatus().getStatus());
            if (responseType == null || responseType.isAny()) {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getValue());
            }
            response.setContentLength(((byte[]) any).length);
            try (var outputStream = response.getOutputStream()) {
                outputStream.write((byte[]) any);
                outputStream.flush();
            } catch (IOException e) {
                LOGGER.error(" ", e);
            }
        } else {
            try {
                response.setStatus(routerResponse.getStatus().getStatus());
                if (responseType == null) {
                    responseType = defaultResponseType;
                }
                response.setContentType(responseType.toString());
                response.getWriter().write(String.valueOf(any));
            } catch (IOException e) {
                LOGGER.error(" ", e);
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletResponseHandler.class);
}
