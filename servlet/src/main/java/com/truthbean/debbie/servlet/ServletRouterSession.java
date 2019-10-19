package com.truthbean.debbie.servlet;

import com.truthbean.debbie.mvc.RouterSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/15 22:17.
 */
public class ServletRouterSession implements RouterSession {

    private final HttpSession session;

    public ServletRouterSession(HttpServletRequest request) {
        this(request.getSession());
    }

    public ServletRouterSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public Long getCreateTime() {
        return session.getCreationTime();
    }

    @Override
    public Long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public Long getMaxInactiveInterval() {
        return Long.valueOf(session.getMaxInactiveInterval());
    }

    @Override
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    @Override
    public void invalidate() {
        session.invalidate();
    }

    @Override
    public Set<String> getAttributeNames() {
        Set<String> result = new HashSet<>();
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            result.add(names.nextElement());
        }
        return result;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, session.getAttribute(name));
        }
        return map;
    }
}
