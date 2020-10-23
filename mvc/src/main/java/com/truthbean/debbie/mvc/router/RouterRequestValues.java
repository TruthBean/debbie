/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

import java.io.InputStream;
import java.net.HttpCookie;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 08:33.
 */
public class RouterRequestValues {
    private Map<String, List<String>> pathAttributes;
    private Map<String, List<String>> matrixAttributes;

    private Map<String, List<String>> queries;
    private Map<String, List<Object>> params;
    private Map<String, List<String>> headers;
    private Map<String, List<Object>> cookieAttributes;
    private Map<String, Object> sessionAttributes;
    private Map<String, Object> innerAttributes;
    private InputStream body;
    private String textBody;

    private final RouterSession routerSession;
    private final RouterRequest routerRequest;
    private final RouterResponse routerResponse;

    private final Map<String, List<Object>> mixValues = new HashMap<>();

    public RouterRequestValues(RouterRequest routerRequest, RouterResponse routerResponse) {

        this.routerRequest = routerRequest;
        routerSession = routerRequest.getSession();
        this.routerResponse = routerResponse;
    }

    public Map<String, List<String>> getQueries() {
        if (queries == null) {
            queries = new HashMap<>();
            var requestQueries = routerRequest.getQueries();
            if (requestQueries != null)
                queries.putAll(requestQueries);
        }
        return Collections.unmodifiableMap(queries);
    }

    public Map<String, List<String>> getMatrixAttributes() {
        if (matrixAttributes == null) {
            matrixAttributes = new HashMap<>();
            Map<String, List<String>> matrix = routerRequest.getMatrix();
            if (matrix != null && !matrix.isEmpty())
                matrixAttributes.putAll(matrix);
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

    public Map<String, List<Object>> getParams() {
        if (params == null) {
            params = routerRequest.getParameters();
        }
        return params;
    }

    public Map<String, List<String>> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
            var requestHeaders = routerRequest.getHeader().getHeaders();
            if (requestHeaders != null)
                headers.putAll(requestHeaders);
        }
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, List<Object>> getCookieAttributes() {
        if (cookieAttributes == null) {
            cookieAttributes = new HashMap<>();
            List<HttpCookie> cookies = routerRequest.getCookies();
            if (cookies != null && !cookies.isEmpty())
                setCookieAttributes(cookies);
        }
        return Collections.unmodifiableMap(cookieAttributes);
    }

    private void setCookieAttributes(List<HttpCookie> cookies) {
        for (int i = 0; i < cookies.size(); i++) {
            HttpCookie iCookie = cookies.get(i);
            List<Object> value = new ArrayList<>();
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
            if (attributes != null && !attributes.isEmpty())
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

    public Map<String, List<String>> getPathAttributes() {
        if (pathAttributes == null) {
            pathAttributes = new HashMap<>();
            Map<String, List<String>> pathAttributes = routerRequest.getPathAttributes();
            if (pathAttributes != null && !pathAttributes.isEmpty())
                this.pathAttributes.putAll(pathAttributes);
        }
        return pathAttributes;
    }

    private void setMixValues() {
        var queries = getQueries();
        if (queries != null && !queries.isEmpty()) {
            queries.forEach(this::addMixValue);
        }

        var params = getParams();
        if (params != null && !params.isEmpty()) {
            mixValues.putAll(params);
        }

        var headers = getHeaders();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(this::addMixValue);
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
            pathAttributes.forEach(this::addMixValue);
        }

        var matrixAttributes = getMatrixAttributes();
        if (matrixAttributes != null && !matrixAttributes.isEmpty()) {
            matrixAttributes.forEach(this::addMixValue);
        }

        var innerAttributes = getInnerAttributes();
        if (innerAttributes != null && !innerAttributes.isEmpty()) {
            for (Map.Entry<String, Object> entry : innerAttributes.entrySet()) {
                mixValues.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
        }
    }

    public Map<String, List<Object>> getMixValues() {
        if (mixValues.isEmpty()) {
            setMixValues();
        }
        return Collections.unmodifiableMap(mixValues);
    }

    private void addMixValue(String name, List<?> value) {
        List<Object> values = new ArrayList<>(value);
        mixValues.put(name, values);
    }
}
