/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.server.session;

import com.truthbean.debbie.mvc.RouterSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Simple in-memory implementation of {@link RouterSession} backed by a
 * {@link HashMap} for attributes.
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/25 12:11.
 */
public class SimpleRouterSession implements RouterSession {

    /** session id (random UUID) */
    private final String id;

    /** session creation time in milliseconds */
    private final Long createTime;

    /** last accessed time in milliseconds */
    private Long lastAccessedTime;

    /** max inactive interval in milliseconds */
    private Long maxInactiveInterval;

    /** session attributes */
    private final Map<String, Object> attributes;

    /** whether this session has been invalidated */
    private boolean invalidate;

    /** Creates a new session with a random UUID id and the current time. */
    public SimpleRouterSession() {
        this.id = UUID.randomUUID().toString();
        this.createTime = System.currentTimeMillis();
        this.attributes = new HashMap<>();
        this.invalidate = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    @Override
    public Long getLastAccessedTime() {
        return lastAccessedTime;
    }

    /** Sets the last accessed time. */
    public void setLastAccessedTime(Long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Override
    public Long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    /** Sets the max inactive interval. */
    public void setMaxInactiveInterval(Long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void invalidate() {
        invalidate = true;
        this.attributes.clear();
    }

    /** Returns whether this session has been invalidated. */
    public boolean isInvalidate() {
        return invalidate;
    }

    @Override
    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
}
