package com.truthbean.code.debbie.undertow;

import com.truthbean.code.debbie.mvc.RouterSession;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.util.Sessions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/12 23:32.
 */
public class UndertowRouterSession implements RouterSession {
    private Session session;
    private HttpServerExchange exchange;

    public UndertowRouterSession(HttpServerExchange exchange) {
        this.session = Sessions.getSession(exchange);
        this.exchange = exchange;
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
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public long getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
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
        session.invalidate(exchange);
    }

    @Override
    public Set<String> getAttributeNames() {
        return session.getAttributeNames();
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> map = new HashMap<>();
        Set<String> names = session.getAttributeNames();
        for (String name : names) {
            map.put(name, session.getAttribute(name));
        }
        return map;
    }
}
