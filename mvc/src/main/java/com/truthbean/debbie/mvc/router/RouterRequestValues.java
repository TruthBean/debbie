package com.truthbean.debbie.mvc.router;


import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

import java.io.InputStream;
import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 08:33.
 */
public class RouterRequestValues {
    private Map<String, List> pathAttributes;
    private Map<String, List<String>> matrixAttributes;

    private Map<String, List<String>> queries;
    private Map<String, List> params;
    private Map<String, List<String>> headers;
    private Map<String, List> cookieAttributes;
    private Map<String, Object> sessionAttributes;
    private Map<String, Object> innerAttributes;
    private InputStream body;
    private String textBody;

    private RouterSession routerSession;
    private RouterRequest routerRequest;
    private RouterResponse routerResponse;

    private final Map<String, List> mixValues = new HashMap<>();

    public RouterRequestValues(RouterRequest routerRequest, RouterResponse routerResponse) {

        this.routerRequest = routerRequest;
        routerSession = routerRequest.getSession();
        this.routerResponse = routerResponse;
    }

    public Map<String, List> getQueries() {
        if (queries == null) {
            queries = new HashMap<>();
            var requestQueries = routerRequest.getQueries();
            if (requestQueries != null)
                queries.putAll(requestQueries);
        }
        return Collections.unmodifiableMap(queries);
    }

    public Map<String, List> getMatrixAttributes() {
        if (matrixAttributes == null) {
            matrixAttributes = new HashMap<>();
            matrixAttributes.putAll(routerRequest.getMatrix());
        }
        return Collections.unmodifiableMap(matrixAttributes);
    }

    public RouterSession getRouterSession() {
        return routerSession;
    }

    public RouterRequest getRouterRequest() {
        return routerRequest;
    }

    public RouterResponse getRouterResponse() {
        return routerResponse;
    }

    public Map<String, List> getParams() {
        if (params == null) {
            params = routerRequest.getParameters();
        }
        return params;
    }

    public Map<String, List> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
            var requestHeaders = routerRequest.getHeader().getHeaders();;
            if (requestHeaders != null)
                headers.putAll(requestHeaders);
        }
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, List> getCookieAttributes() {
        if (cookieAttributes == null) {
            cookieAttributes = new HashMap<>();
            setCookieAttributes(routerRequest.getCookies());
        }
        return Collections.unmodifiableMap(cookieAttributes);
    }

    private void setCookieAttributes(List<HttpCookie> cookies) {
        for (int i = 0; i < cookies.size(); i++) {
            HttpCookie iCookie = cookies.get(i);
            List value = new ArrayList();
            for (int j = i; j < cookies.size(); j++) {
                HttpCookie httpCookie = cookies.get(j);
                if (iCookie.getName().equalsIgnoreCase(httpCookie.getName())) {
                    value.add(httpCookie.getValue());
                }
            }
            this.cookieAttributes.put(iCookie.getName(), value);
        }
    }

    public Map<String, Object> getSessionAttributes() {
        if (sessionAttributes == null && routerSession != null) {
            sessionAttributes = new HashMap<>();
            var attributes = routerRequest.getSession().getAttributes();
            if (attributes != null)
                sessionAttributes.putAll(attributes);
        }
        if (sessionAttributes == null) {
            return new HashMap<>(0);
        }
        return Collections.unmodifiableMap(sessionAttributes);
    }

    public Map<String, Object> getInnerAttributes() {
        innerAttributes = routerRequest.getAttributes();
        return Collections.unmodifiableMap(innerAttributes);
    }

    public InputStream getBody() {
        if (body == null) {
            body = routerRequest.getInputStreamBody();
        }
        return body;
    }

    public String getTextBody() {
        if (textBody == null) {
            textBody = routerRequest.getTextBody();
        }
        return textBody;
    }

    public Map<String, List> getPathAttributes() {
        if (pathAttributes == null) {
            pathAttributes = new HashMap<>();
            this.pathAttributes.putAll(routerRequest.getPathAttributes());
        }
        return pathAttributes;
    }

    private void setMixValues() {
        var queries = getQueries();
        if (queries != null && !queries.isEmpty()) {
            mixValues.putAll(queries);
        }

        var params = getParams();
        if (params != null && !params.isEmpty()) {
            mixValues.putAll(params);
        }

        var headers = getHeaders();
        if (headers != null && !headers.isEmpty()) {
            mixValues.putAll(headers);
        }

        var cookieAttributes = getCookieAttributes();
        if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
            mixValues.putAll(cookieAttributes);
        }

        var sessionAttributes = getSessionAttributes();
        if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
            for (Map.Entry<String, Object> entry : sessionAttributes.entrySet()) {
                mixValues.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
        }

        var pathAttributes = getPathAttributes();
        if (pathAttributes != null && !pathAttributes.isEmpty()) {
            mixValues.putAll(pathAttributes);
        }

        var matrixAttributes = getMatrixAttributes();
        if (matrixAttributes != null && !matrixAttributes.isEmpty()) {
            mixValues.putAll(matrixAttributes);
        }

        var innerAttributes = getInnerAttributes();
        if (innerAttributes != null && !innerAttributes.isEmpty()) {
            for (Map.Entry<String, Object> entry : innerAttributes.entrySet()) {
                mixValues.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
        }
    }

    public Map<String, List> getMixValues() {
        if (mixValues.isEmpty()) {
            setMixValues();
        }
        return Collections.unmodifiableMap(mixValues);
    }
}
