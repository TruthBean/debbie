package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.mvc.RouterCookie;
import com.truthbean.code.debbie.mvc.RouterSession;
import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.url.RouterPathAttribute;

import java.io.InputStream;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 08:33.
 */
public class RouterRequestValues {
    private Map<String, List> pathAttributes;

    private Map<String, List<String>> queries;
    private Map<String, List> params;
    private Map<String, List<String>> headers;
    private Map<String, List> cookieAttributes;
    private Map<String, Object> sessionAttributes;
    private InputStream body;

    private RouterSession routerSession;
    private RouterRequest routerRequest;

    private final Map<String, List> mixValues = new HashMap<>();

    public RouterRequestValues(RouterRequest routerRequest) {
        pathAttributes = new HashMap<>();
        setPathAttributes(routerRequest.getPathAttributes());

        queries = routerRequest.getQueries();
        params = routerRequest.getParams();
        headers = routerRequest.getHeaders();

        cookieAttributes = new HashMap<>();
        setCookieAttributes(routerRequest.getCookies());

        routerSession = routerRequest.getSession();
        sessionAttributes = routerRequest.getSession().getAttributes();

        setMixValues();
        body = routerRequest.getBody();

        this.routerRequest = routerRequest;
    }

    public Map<String, List> getQueries() {
        if (queries == null) {
            queries = new HashMap<>();
        }
        return Collections.unmodifiableMap(queries);
    }

    public RouterSession getRouterSession() {
        return routerSession;
    }

    public RouterRequest getRouterRequest() {
        return routerRequest;
    }

    public Map<String, List> getParams() {
        return params;
    }

    public void setParams(Map<String, List> params) {
        this.params = params;
    }

    public Map<String, List> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, List> getCookieAttributes() {
        if (cookieAttributes == null) {
            cookieAttributes = new HashMap<>();
        }
        return Collections.unmodifiableMap(cookieAttributes);
    }

    private void setCookieAttributes(List<RouterCookie> cookies) {
        for (int i = 0; i< cookies.size(); i++) {
            RouterCookie iCookie = cookies.get(i);
            List value = new ArrayList();
            for (int j = i; j< cookies.size(); j++) {
                RouterCookie routerCookie = cookies.get(j);
                if (iCookie.getName().equalsIgnoreCase(routerCookie.getName())) {
                    value.add(routerCookie.getValue());
                }
            }
            this.cookieAttributes.put(iCookie.getName(), value);
        }
    }

    public Map<String, Object> getSessionAttributes() {
        return Collections.unmodifiableMap(sessionAttributes);
    }

    public InputStream getBody() {
        return body;
    }

    private void setPathAttributes(List<RouterPathAttribute> pathAttributes) {
        for (RouterPathAttribute pathAttribute: pathAttributes) {
            this.pathAttributes.put(pathAttribute.getName(), pathAttribute.getValue());
        }
    }

    public Map<String, List> getPathAttributes() {
        return pathAttributes;
    }

    private void setMixValues() {
        if (queries != null && !queries.isEmpty()) {
            mixValues.putAll(queries);
        }
        if (params != null && !params.isEmpty()) {
            mixValues.putAll(params);
        }
        if (headers != null && !headers.isEmpty()) {
            mixValues.putAll(headers);
        }
        if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
            mixValues.putAll(cookieAttributes);
        }
        if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
            for (Map.Entry<String, Object> entry: sessionAttributes.entrySet()) {
                mixValues.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
        }
    }

    public Map<String, List> getMixValues() {
        return Collections.unmodifiableMap(mixValues);
    }
}
