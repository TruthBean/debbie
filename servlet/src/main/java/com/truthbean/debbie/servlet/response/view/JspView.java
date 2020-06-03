/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.response.view;

import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.mvc.response.view.NoViewRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 21:16.
 */
public class JspView extends AbstractTemplateView {
    private HttpServletRequest httpServletRequest;

    private HttpServletResponse httpServletResponse;

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    public void setModelAndView(AbstractTemplateView modelAndView) {
        from(modelAndView);
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public AbstractTemplateView getTemplateView() {
        return this;
    }

    private void forward() {
        Map<String, Object> data = getAttributes();
        if (!data.isEmpty()) {
            data.forEach((key, value) -> httpServletRequest.setAttribute(key, value));
        }

        try {
            httpServletRequest.getRequestDispatcher(getLocation()).forward(httpServletRequest, httpServletResponse);
        } catch (ServletException | IOException e) {
            LOGGER.error("servlet forward error", e);
        }
    }

    private void redirect() {
        try {
            httpServletResponse.sendRedirect(getTemplate());
        } catch (IOException e) {
            LOGGER.error("servlet redirect error", e);
        }
    }

    public void transfer() {
        if (super.isRedirect()) {
            redirect();
        } else {
            forward();
        }
    }

    @Override
    public NoViewRender render() {
        return new NoViewRender();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JspView.class);
}