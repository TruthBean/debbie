package com.truthbean.debbie.mvc.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpHeader {
    private final Map<String, List<String>> headers = new HashMap<>();

    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public List<String> getHeaders(String name) {
        return headers.get(name);
    }

    public String getHeader(String name) {
        var values = headers.get(name);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    public void addHeaders(Map<String, List<String>> headers) {
        this.headers.putAll(headers);
    }

    public void addHeader(String name, List<String> value) {
        this.headers.put(name, value);
    }

    public HttpHeader copy() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.headers.putAll(this.headers);
        return httpHeader;
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public String toString() {
        return headers.toString();
    }

    public enum HttpHeaderNames {
        // todo
    }
}
