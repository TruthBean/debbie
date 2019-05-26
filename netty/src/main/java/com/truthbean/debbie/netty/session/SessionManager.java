package com.truthbean.debbie.netty.session;

import com.truthbean.debbie.mvc.RouterSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/25 12:08.
 */
public class SessionManager {
    private Map<String, RouterSession> sessionMap;

    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    public RouterSession createSession() {
        var session = new NettyRouterSession();
        this.sessionMap.put(session.getId(), session);
        return session;
    }

    public RouterSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }
}
