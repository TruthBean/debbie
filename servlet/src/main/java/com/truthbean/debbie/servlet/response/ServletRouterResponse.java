package com.truthbean.debbie.servlet.response;

import com.truthbean.debbie.mvc.response.RouterResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ServletRouterResponse extends RouterResponse {
    private HttpServletResponse response;

    public ServletRouterResponse() {
    }

    public ServletRouterResponse(HttpServletResponse response) {
        this.response = response;
    }

    public ServletRouterResponse(RouterResponse copy) {
        copyFrom(copy);
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
