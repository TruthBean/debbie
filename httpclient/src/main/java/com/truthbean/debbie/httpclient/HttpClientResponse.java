/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.mvc.request.HttpHeader;

import javax.net.ssl.SSLSession;
import java.net.http.HttpClient;
import java.util.ArrayList;
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

    public List<String> getHeaderValues(HttpHeader.HttpHeaderName headerName) {
        return getHeaderValues(headerName.getName());
    }

    public List<String> getHeaderValues(String headerName) {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                var key = entry.getKey();
                if (key.equalsIgnoreCase(headerName)) {
                    return entry.getValue();
                }
            }
        }
        return new ArrayList<>();
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
