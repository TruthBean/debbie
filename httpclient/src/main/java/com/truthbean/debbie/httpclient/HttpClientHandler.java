/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.lang.NonNull;
import com.truthbean.debbie.mvc.request.HttpMethod;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientHandler extends HttpClientAction {

    public HttpClientHandler(HttpClientConfiguration configuration) {
        super(configuration);
    }

    public String get(String url) {
        return get(url, new HashMap<>(0));
    }

    public String get(String url, List<HttpCookie> cookies) {
        return (String) get(url, cookies, HttpResponseType.STRING);
    }

    public Object get(String url, List<HttpCookie> cookies, HttpResponseType responseType) {
        return doHttp(url, HttpMethod.GET, null, cookies, null, responseType);
    }

    public  <T> T getBody(HttpClientResponse<T> map) {
        if (map != null && map.hasBody()) {
            return map.getBody();
        }
        return null;
    }

    public String get(String url, Map<String, String> headers) {
        HttpClientResponse<String> response = doGet(url, headers, HttpResponseType.STRING);
        return getBody(response);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doGet(String url, Map<String, String> headers, HttpResponseType responseType) {
        return doHttp(url, HttpMethod.GET, headers, null, null, responseType);
    }

    // =================================================================================================================

    public String post(String url, String body) {
        return post(url, body, null);
    }

    public String post(String url, String body, Map<String, String> headers) {
        HttpClientResponse<String> response = doPost(url, body, headers);
        return getBody(response);
    }

    public String post(String url, byte[] body, Map<String, String> headers) {
        HttpClientResponse<String> response = doPost(url, body, headers);
        return getBody(response);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doPost(String url, String body, Map<String, String> headers) {
        return doPost(url, body, headers, HttpResponseType.STRING);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doPost(String url, byte[] body, Map<String, String> headers) {
        return doPost(url, body, headers, HttpResponseType.STRING);
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doPost(String url, String body, Map<String, String> headers,
                                     HttpResponseType responseType) {
        return doHttp(url, HttpMethod.POST, headers, null, body.getBytes(), responseType);
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doPost(String url, byte[] body, Map<String, String> headers,
                                     HttpResponseType responseType) {
        return doHttp(url, HttpMethod.POST, headers, null, body, responseType);
    }

    // =================================================================================================================

    public String put(String url, String body) {
        return put(url, body, null);
    }

    public String put(String url, String body, Map<String, String> headers) {
        HttpClientResponse<String> response = doPut(url, body, headers);
        return getBody(response);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doPut(String url, String body, Map<String, String> headers) {
        return doPut(url, body, headers, HttpResponseType.STRING);
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doPut(String url, String body, Map<String, String> headers,
                                    HttpResponseType responseType) {
        return doHttp(url, HttpMethod.PUT, headers, null, body.getBytes(), responseType);
    }

    // =================================================================================================================

    public String delete(String url) {
        return delete(url, null, null);
    }

    public String delete(String url, String body) {
        return delete(url, body, null);
    }

    public String delete(String url, Map<String, String> headers) {
        return delete(url, "", headers);
    }

    public String delete(String url, String body, Map<String, String> headers) {
        return getBody(doDelete(url, body, headers));
    }

    @SuppressWarnings({"unchecked"})
    public String delete(String url, String body, Map<String, String> headers, HttpResponseType responseType) {
        HttpClientResponse<String> response = doDelete(url, body, headers, responseType);
        return getBody(response);
    }

    public HttpClientResponse<String> doDelete(String url, Map<String, String> headers) {
        return doDelete(url, null, headers);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doDelete(String url, String body, Map<String, String> headers) {
        return doDelete(url, body, headers, HttpResponseType.STRING);
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doDelete(String url, String body, Map<String, String> headers,
                                       HttpResponseType responseType) {
        byte[] bytes = null;
        if (body != null) {
            bytes = body.getBytes();
        }
        return doHttp(url, HttpMethod.DELETE, headers, null, bytes, responseType);
    }

    // =================================================================================================================

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doHttp(String url, HttpMethod method, Map<String, String> queries,
                                     Map<String, String> headers, List<HttpCookie> cookies,
                                     byte[] body,
                                     HttpResponseType responseType) {
        StringBuilder queryStringBuilder = new StringBuilder();
        if (queries != null && !queries.isEmpty()) {
            StringUtils.joining(queries, "&", "=", queryStringBuilder);
            String finalUrl = url + "?" + queryStringBuilder;
            return doHttp(finalUrl, method, headers, cookies, body, responseType);
        }
        return doHttp(url, method, headers, cookies, body, responseType);
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doHttp(String url, @NonNull HttpMethod method,
                                     Map<String, String> headers, List<HttpCookie> cookies,
                                     byte[] body,
                                     HttpResponseType responseType) {
        long startTime = System.nanoTime();
        LOGGER.debug(() -> OPERATION_NAME + "开始" + method.name() + "通信，目标url: " + url + " ，内容：" + Arrays.toString(body));
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }
        if (cookies != null && !cookies.isEmpty()) {
            var cookie = buildCookies(cookies);
            LOGGER.debug(() -> "request Cookie: " + cookie);
            builder = builder.header("Cookie", cookie);
        }
        HttpRequest.BodyPublisher bodyPublisher;
        if (body == null || body.length == 0) {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        } else {
            bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(body);
        }
        HttpRequest httpRequest = builder.method(method.name(), bodyPublisher).build();
        return action(httpRequest, startTime, responseType);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);
}
