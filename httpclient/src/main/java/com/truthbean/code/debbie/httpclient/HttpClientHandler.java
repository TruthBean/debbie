package com.truthbean.code.debbie.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientHandler extends HttpHandler {
    private HttpClient httpClient;

    public HttpClientHandler(HttpClientConfiguration configuration) {
        super(configuration);
        this.httpClient = createHttpClient();
    }

    private HttpClientConfiguration configuration = super.getConfiguration();

    public String get(String url) {
        return get(url, new HashMap<>());
    }

    public String get(String url, List<HttpCookie> cookies) {
        long startTime = System.nanoTime();
        LOGGER.debug(OPERATION_NAME + "开始GET通信，目标url: " + url);
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        if (cookies != null && !cookies.isEmpty()) {
            var cookie = buildCookies(cookies);
            LOGGER.debug("request Cookie: " + cookie);
            builder = builder.header("Cookie", cookie);
        }
        HttpRequest httpRequest = builder.uri(URI.create(url)).build();
        Map<String, String> action = action(httpRequest, startTime);
        return action(action);
    }

    private String action(Map<String, String> map) {
        if (map != null && map.containsKey("body")) {
            return map.get("body");
        }
        return null;
    }

    public String get(String url, Map<String, String> headers) {
        Map<String, String> action = doGet(url, headers);
        return action(action);
    }

    public Map<String, String> doGet(String url, Map<String, String> headers) {
        long startTime = System.nanoTime();
        LOGGER.debug(OPERATION_NAME + "开始GET通信，目标url: " + url);
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }
        HttpRequest httpRequest = builder.uri(URI.create(url)).build();
        return action(httpRequest, startTime);
    }

    // =================================================================================================================

    public String post(String url, String jsonContent) {
        return post(url, jsonContent, null);
    }

    public String post(String url, String jsonContent, Map<String, String> headers) {
        Map<String, String> action = doPost(url, jsonContent, headers);
        return action(action);
    }

    public Map<String, String> doPost(String url, String jsonContent,
                                             Map<String, String> headers) {
        long startTime = System.nanoTime();
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
        return action(httpRequest, startTime);
    }

    // =================================================================================================================

    public String put(String url, String jsonContent) {
        return put(url, jsonContent, null);
    }

    public String put(String url, String jsonContent,
                             Map<String, String> headers) {
        Map<String, String> action = doPut(url, jsonContent, headers);
        return action(action);
    }

    public Map<String, String> doPut(String url, String jsonContent,
                                            Map<String, String> headers) {
        long startTime = System.nanoTime();
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
        return action(httpRequest, startTime);
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

    public String delete(String url, String jsonContent,
                                Map<String, String> headers) {
        return action(doDelete(url, jsonContent, headers));
    }

    public Map<String, String> doDelete(String url,
                                               Map<String, String> headers) {
        return doDelete(url, null, headers);
    }

    public Map<String, String> doDelete(String url, String jsonContent,
                                               Map<String, String> headers) {
        long startTime = System.nanoTime();
        LOGGER.debug(OPERATION_NAME + "开始DELETE通信，目标url: " + url + " ，内容：" + jsonContent);
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
            httpRequest = builder.method("delete", body).build();
        }
        return action(httpRequest, startTime);
    }

    // =====================================================================================================

    private static final HttpClient.Builder HTTP_CLIENT_BUILDER = HttpClient.newBuilder();

    private HttpClient createHttpClient() {
        HttpClient.Builder builder = HTTP_CLIENT_BUILDER;
        if (configuration.useProxy()) {
            builder.proxy(createProxySelector());
        }
        if (configuration.needAuth()) {
            builder.authenticator(basicAuth());
        }
        if (configuration.isInsecure()) {
            builder.sslContext(createSSLContext());
        }
        return builder.build();
    }

    private <T> HttpResponse<T> getResponse(CompletableFuture<HttpResponse<T>> future) {
        HttpResponse<T> response = null;
        try {
            response = future.get(configuration.getResponseTimeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.error("response error. ", e);
        }
        return response;
    }

    private <T> HttpResponse<T> actionWithRetryWhenFail(CompletableFuture<HttpResponse<T>> future) {
        int tryCount = 0;
        HttpResponse<T> response = null;
        while ((response == null) && tryCount < configuration.getRetryTime()) {
            LOGGER.debug("HttpRequest is not successful, retry " + tryCount + " time");
            tryCount++;
            // retry the HttpRequest
            future = future.copy();
            response = getResponse(future);
            if (response != null) {
                try {
                    LOGGER.debug(OPERATION_NAME + "通讯完成，返回码：" + response.statusCode());
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
        return response;
    }

    private Map<String, String> action(HttpRequest httpRequest, long startTime) {
        Map<String, String> result = null;
        CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response;
        if (configuration.retry()) {
            response = actionWithRetryWhenFail(future);
        } else {
            response = getResponse(future);
        }

        if (response != null) {
            result = new HashMap<>();
            LOGGER.debug(OPERATION_NAME + "通讯完成，返回码：" + response.statusCode());
            result.put("code", String.valueOf(response.statusCode()));
            if (response.body() != null) {
                String responseBody = response.body();
                if (responseBody != null) {
                    try {
                        HttpHeaders responseHeaders = response.headers();
                        LOGGER.debug(
                                "whether it's compressed，value is " + responseHeaders.allValues("Content-Encoding"));
                        result.put("body", responseBody);
                    } catch (Exception e) {
                        LOGGER.error("通讯成功，解析返回值异常", e);
                    }
                    LOGGER.debug(OPERATION_NAME + "返回内容：" + result);
                } else {
                    LOGGER.error("通讯成功，返回内容为空");
                }
            } else {
                LOGGER.error("通讯异常");
            }
        } else {
            LOGGER.error("通讯失败");
        }

        long endTime = System.nanoTime();
        LOGGER.debug(OPERATION_NAME + "共计耗时:" + ((endTime - startTime) / 1000000.0) + "ms");
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);
}
