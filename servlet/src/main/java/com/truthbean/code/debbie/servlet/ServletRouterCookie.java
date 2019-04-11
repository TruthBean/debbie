package com.truthbean.code.debbie.servlet;

import javax.servlet.http.Cookie;
import java.net.HttpCookie;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/24 11:34.
 */
public class ServletRouterCookie {
    private HttpCookie httpCookie;

    public ServletRouterCookie(Cookie cookie) {
        httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
        httpCookie.setValue(cookie.getValue());
        httpCookie.setPath(cookie.getPath());
        httpCookie.setDomain(cookie.getDomain());
        httpCookie.setMaxAge(cookie.getMaxAge());
        httpCookie.setDiscard(cookie.getMaxAge() < 0);
        httpCookie.setSecure(cookie.getSecure());
        httpCookie.setVersion(cookie.getVersion());
        httpCookie.setHttpOnly(cookie.isHttpOnly());
        httpCookie.setComment(cookie.getComment());
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
