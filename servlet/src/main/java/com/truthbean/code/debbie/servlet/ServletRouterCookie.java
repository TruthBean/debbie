package com.truthbean.code.debbie.servlet;

import com.truthbean.code.debbie.mvc.RouterCookie;

import javax.servlet.http.Cookie;

import java.util.Date;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/24 11:34.
 */
public class ServletRouterCookie extends RouterCookie {
    public ServletRouterCookie(Cookie cookie) {
        this.setName(cookie.getName());
        this.setValue(cookie.getValue());
        this.setPath(cookie.getPath());
        this.setDomain(cookie.getDomain());
        this.setMaxAge(cookie.getMaxAge());
        this.setDiscard(cookie.getMaxAge() < 0);
        this.setSecure(cookie.getSecure());
        this.setVersion(cookie.getVersion());
        this.setHttpOnly(cookie.isHttpOnly());
        if ("Expires".equals(cookie.getName().toLowerCase())) {
            this.setExpires(new Date(Date.parse(cookie.getValue())));
        }
        this.setComment(cookie.getComment());
    }
}
