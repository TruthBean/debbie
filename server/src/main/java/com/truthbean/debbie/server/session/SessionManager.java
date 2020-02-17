package com.truthbean.debbie.server.session;

import com.truthbean.debbie.mvc.RouterSession;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:16
 */
public interface SessionManager {

    /**
     * @return a new session
     */
    RouterSession createSession();

    /**
     * get session from sessionManager
     * @param sessionId session id
     * @return RouterSession
     */
    RouterSession getSession(String sessionId);
}
