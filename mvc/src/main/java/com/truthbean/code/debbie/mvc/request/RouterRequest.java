package com.truthbean.code.debbie.mvc.request;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.mvc.RouterCookie;
import com.truthbean.code.debbie.mvc.RouterSession;
import com.truthbean.code.debbie.mvc.url.RouterPathAttribute;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-12-15 12:56.
 */
public class RouterRequest implements Cloneable {

    private HttpMethod method;

    private String url;

    private List<RouterPathAttribute> pathAttributes;

    private Map<String, List<String>> headers;

    private List<RouterCookie> cookies;

    private RouterSession session;

    private MediaType bodyType;

    private Map<String, List> params;

    private Map<String, List<String>> queries;

    private InputStream body;

    private MediaType contentType;

    private MediaType responseTypeInHeader;

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<RouterPathAttribute> getPathAttributes() {
        return pathAttributes;
    }

    public void setPathAttributes(List<RouterPathAttribute> pathAttributes) {
        this.pathAttributes = pathAttributes;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public List<RouterCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<RouterCookie> cookies) {
        this.cookies = cookies;
    }

    public RouterSession getSession() {
        return session;
    }

    public void setSession(RouterSession session) {
        this.session = session;
    }

    public MediaType getBodyType() {
        return bodyType;
    }

    public void setBodyType(MediaType bodyType) {
        this.bodyType = bodyType;
    }

    public Map<String, List> getParams() {
        return params;
    }

    public void setParams(Map<String, List> params) {
        this.params = params;
    }

    public Map<String, List<String>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, List<String>> queries) {
        this.queries = queries;
    }

    public InputStream getBody() {
        return body;
    }

    public void setBody(InputStream body) {
        this.body = body;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public MediaType getResponseTypeInHeader() {
        return responseTypeInHeader;
    }

    public void setResponseTypeInHeader(MediaType responseTypeInHeader) {
        this.responseTypeInHeader = responseTypeInHeader;
    }

    @Override
    public RouterRequest clone() {
        RouterRequest clone;
        try {
            clone = (RouterRequest) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            clone = new RouterRequest();
            clone.method = method;
            clone.url = url;
            clone.bodyType = bodyType;
            clone.body = body;
            clone.contentType = contentType;
            clone.responseTypeInHeader = responseTypeInHeader;
        }

        clone.pathAttributes = new ArrayList<>(pathAttributes);
        clone.headers = new HashMap<>(headers);
        clone.cookies = new ArrayList<>(cookies);
        clone.session = session;
        clone.params = new HashMap<>(params);
        clone.queries = new HashMap<>(queries);
        return clone;
    }
}
