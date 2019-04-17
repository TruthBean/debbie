package com.truthbean.debbie.mvc.csrf;

import com.truthbean.debbie.mvc.request.RouterRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultCsrfTokenFactory implements CsrfTokenFactory {
    private Map<String, CsrfToken> tokens = new HashMap<>();

    @Override
    public CsrfToken loadToken(RouterRequest request) {
        var id = request.getId();
        if (tokens.containsKey(id)) {
            return tokens.get(id);
        } else {
            var token = CsrfToken.create();
            tokens.put(id, token);
            return token;
        }
    }
}
