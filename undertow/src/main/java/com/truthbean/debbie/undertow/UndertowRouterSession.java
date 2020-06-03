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

import com.truthbean.debbie.mvc.RouterSession;
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
    private final Session session;
    private final HttpServerExchange exchange;

    public UndertowRouterSession(HttpServerExchange exchange) {
        this.session = Sessions.getOrCreateSession(exchange);
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
        if (names != null && !names.isEmpty())
            for (String name : names) {
                map.put(name, session.getAttribute(name));
            }
        return map;
    }
}
