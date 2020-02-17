package com.truthbean.debbie.server.session;

import com.truthbean.debbie.mvc.RouterSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/25 12:08.
 */
public class SimpleSessionManager implements SessionManager {
    private Map<String, RouterSession> sessionMap;

    public SimpleSessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    @Override
    public RouterSession createSession() {
        var session = new SimpleRouterSession();
        this.sessionMap.put(session.getId(), session);
        return session;
    }

    @Override
    public RouterSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }
}
