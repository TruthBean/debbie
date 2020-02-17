package com.truthbean.debbie.httpclient;

import javax.net.ssl.SSLSession;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-23 00:13.
 */
public class HttpClientResponse<T> {
    private int code;

    private T body;

    public Map<String, List<String>> headers;

    public SSLSession sslSession;

    public HttpClient.Version version;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean hasBody() {
        return body != null;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public SSLSession getSslSession() {
        return sslSession;
    }

    public void setSslSession(SSLSession sslSession) {
        this.sslSession = sslSession;
    }

    public HttpClient.Version getVersion() {
        return version;
    }

    public void setVersion(HttpClient.Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "{" + "\"code\":" + code + "," + "\"body\":" + body + "," + "\"headers\":" + headers + ","
                + "\"sslSession\":" + sslSession + "," + "\"version\":" + version + "}";
    }
}
