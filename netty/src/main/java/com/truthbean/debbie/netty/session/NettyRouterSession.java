package com.truthbean.debbie.netty.session;

import com.truthbean.debbie.mvc.RouterSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/25 12:11.
 */
public class NettyRouterSession implements RouterSession {

    private String id;

    private Long createTime;

    private Long lastAccessedTime;

    private Long maxInactiveInterval;

    private Map<String, Object> attributes;

    private boolean invalidate;

    public NettyRouterSession() {
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
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Override
    public long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

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
