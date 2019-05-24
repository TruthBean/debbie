package com.truthbean.debbie.servlet;

import javax.servlet.http.Cookie;
import java.net.HttpCookie;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/24 11:34.
 */
public class ServletRouterCookie {
    private HttpCookie httpCookie;
    private Cookie cookie;

    public ServletRouterCookie(Cookie cookie) {
        this.cookie = cookie;
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

    public ServletRouterCookie(HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
        this.cookie = new Cookie(httpCookie.getName(), httpCookie.getValue());
        cookie.setValue(httpCookie.getValue());
        cookie.setPath(httpCookie.getPath());
        var domain = httpCookie.getDomain();
        if (domain != null) {
            cookie.setDomain(httpCookie.getDomain());
        }
        cookie.setMaxAge(Math.toIntExact(httpCookie.getMaxAge()));
        cookie.setSecure(httpCookie.getSecure());
        cookie.setVersion(httpCookie.getVersion());
        cookie.setHttpOnly(httpCookie.isHttpOnly());
        cookie.setComment(httpCookie.getComment());
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public Cookie getCookie() {
        return cookie;
    }
}
