package com.truthbean.debbie.undertow;

import io.undertow.server.handlers.Cookie;

import java.net.HttpCookie;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/12 23:21.
 */
public class UndertowRouterCookie {
    private HttpCookie httpCookie;

    public UndertowRouterCookie(Cookie cookie) {
        httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
        httpCookie.setPath(cookie.getPath());
        httpCookie.setDomain(cookie.getDomain());
        var maxAge = cookie.getMaxAge();
        if (maxAge != null) {
            httpCookie.setMaxAge(maxAge);
            httpCookie.setDiscard(maxAge < 0);
        }
        httpCookie.setSecure(cookie.isSecure());
        httpCookie.setVersion(cookie.getVersion());
        httpCookie.setHttpOnly(cookie.isHttpOnly());
        httpCookie.setComment(cookie.getComment());
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
