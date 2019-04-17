package com.truthbean.debbie.mvc.csrf;

import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RouterRequest;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class SessionCsrfTokenFactory implements CsrfTokenFactory {

    private RouterSession session;

    public SessionCsrfTokenFactory(RouterSession session) {
        this.session = session;
    }

    @Override
    public CsrfToken loadToken(RouterRequest request) {
        var id = request.getId();
        Set<String> keys = session.getAttributeNames();
        if (keys.contains(id)) {
            return (CsrfToken) session.getAttribute(id);
        } else {
            var token = CsrfToken.create();
            session.setAttribute(id, token);
            return token;
        }
    }
}
