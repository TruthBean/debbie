/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return (String) get(url, cookies, MediaType.TEXT_ANY_UTF8.info());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object get(String url, List<HttpCookie> cookies, MediaTypeInfo responseType) {
        long startTime = System.nanoTime();
        LOGGER.debug(() -> OPERATION_NAME + "开始GET通信，目标url: " + url);
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        if (cookies != null && !cookies.isEmpty()) {
            var cookie = buildCookies(cookies);
            LOGGER.debug(() -> "request Cookie: " + cookie);
            builder = builder.header("Cookie", cookie);
        }
        HttpRequest httpRequest = builder.uri(URI.create(url)).build();
        HttpClientResponse action = action(httpRequest, startTime, responseType);
        return action(action);
    }

    private <T> T action(HttpClientResponse<T> map) {
        if (map != null && map.hasBody()) {
            return map.getBody();
        }
        return null;
    }

    public String get(String url, Map<String, String> headers) {
        HttpClientResponse<String> action = doGet(url, headers);
        return action(action);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doGet(String url, Map<String, String> headers) {
        long startTime = System.nanoTime();
        LOGGER.debug(() -> OPERATION_NAME + "开始GET通信，目标url: " + url);
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }
        HttpRequest httpRequest = builder.uri(URI.create(url)).build();
        return action(httpRequest, startTime, MediaType.TEXT_ANY_UTF8.info());
    }

    // =================================================================================================================

    public String post(String url, String jsonContent) {
        return post(url, jsonContent, null);
    }

    public String post(String url, String jsonContent, Map<String, String> headers) {
        HttpClientResponse<String> action = doPost(url, jsonContent, headers);
        return action(action);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doPost(String url, String jsonContent, Map<String, String> headers) {
        return doPost(url, jsonContent, headers, MediaType.TEXT_ANY_UTF8.info());
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doPost(String url, String jsonContent, Map<String, String> headers,
                                      MediaTypeInfo responseType) {
        long startTime = System.nanoTime();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(OPERATION_NAME + "开始POST通信，目标url: " + url + " ，内容：" + jsonContent);
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }
        if (jsonContent == null) {
            jsonContent = "{}";
        }
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonContent);
        HttpRequest httpRequest = builder.POST(body).build();
        return action(httpRequest, startTime, responseType);
    }

    // =================================================================================================================

    public String put(String url, String jsonContent) {
        return put(url, jsonContent, null);
    }

    public String put(String url, String jsonContent,
                             Map<String, String> headers) {
        HttpClientResponse<String> action = doPut(url, jsonContent, headers);
        return action(action);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doPut(String url, String jsonContent, Map<String, String> headers) {
        return doPut(url, jsonContent, headers, MediaType.TEXT_ANY_UTF8.info());
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doPut(String url, String jsonContent, Map<String, String> headers,
                                            MediaTypeInfo responseType) {
        long startTime = System.nanoTime();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(OPERATION_NAME + "开始PUT通信，目标url: " + url + " ，内容：" + jsonContent);
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }
        if (jsonContent == null) {
            jsonContent = "{}";
        }
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonContent);
        HttpRequest httpRequest = builder.PUT(body).build();
        return action(httpRequest, startTime, responseType);
    }

    // =================================================================================================================

    public String delete(String url) {
        return delete(url, null, null);
    }

    public String delete(String url, String jsonContent) {
        return delete(url, jsonContent, null);
    }

    public String delete(String url, Map<String, String> headers) {
        return delete(url, null, headers);
    }

    public String delete(String url, String jsonContent, Map<String, String> headers) {
        return action(doDelete(url, jsonContent, headers));
    }

    public String delete(String url, String jsonContent, Map<String, String> headers, MediaType responseType) {
        HttpClientResponse<String> delete = doDelete(url, jsonContent, headers, responseType.info());
        return action(delete);
    }

    public HttpClientResponse<String> doDelete(String url, Map<String, String> headers) {
        return doDelete(url, null, headers);
    }

    @SuppressWarnings({"unchecked"})
    public HttpClientResponse<String> doDelete(String url, String jsonContent, Map<String, String> headers) {
        return doDelete(url, jsonContent, headers, MediaType.TEXT_ANY_UTF8.info());
    }

    @SuppressWarnings({"rawtypes"})
    public HttpClientResponse doDelete(String url, String jsonContent, Map<String, String> headers,
                                       MediaTypeInfo responseType) {
        long startTime = System.nanoTime();
        LOGGER.debug(() -> OPERATION_NAME + "开始DELETE通信，目标url: " + url + " ，内容：" + jsonContent);
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }
        HttpRequest httpRequest;
        if (jsonContent == null) {
            httpRequest = builder.DELETE().build();
        } else {
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonContent);
            httpRequest = builder.method("DELETE", body).build();
        }
        return action(httpRequest, startTime, responseType);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);
}
