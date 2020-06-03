/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.undertow;

import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

import java.net.HttpCookie;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/12 23:21.
 */
public class UndertowRouterCookie {
    private final HttpCookie httpCookie;
    private final Cookie cookie;

    public UndertowRouterCookie(Cookie cookie) {
        this.cookie = cookie;
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

    public UndertowRouterCookie(HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
        cookie = new CookieImpl(httpCookie.getName(), httpCookie.getValue());
        cookie.setPath(httpCookie.getPath());
        cookie.setDomain(httpCookie.getDomain());
        var maxAge = httpCookie.getMaxAge();
        if (maxAge > 0) {
            cookie.setMaxAge(Math.toIntExact(maxAge));
            cookie.setDiscard(false);
        }
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
