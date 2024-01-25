/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
    private final Map<String, RouterSession> sessionMap;

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
