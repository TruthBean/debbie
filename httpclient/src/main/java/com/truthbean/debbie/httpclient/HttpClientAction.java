package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.net.uri.QueryStringEncoder;
import com.truthbean.debbie.mvc.request.RouterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLParameters;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientAction extends HttpHandler {

    private static final HttpClient.Builder HTTP_CLIENT_BUILDER = HttpClient.newBuilder();

    private HttpClient httpClient;
    private HttpClientConfiguration configuration;

    public HttpClientAction(HttpClientConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
        this.httpClient = createHttpClient();
    }

    protected HttpClient createHttpClient() {
        HttpClient.Builder builder = HTTP_CLIENT_BUILDER;
        if (configuration.useProxy()) {
            builder.proxy(createProxySelector());
        }
        if (configuration.needAuth()) {
            builder.authenticator(basicAuth());
        }
        if (configuration.isInsecure()) {
            builder.sslContext(createSSLContext());
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        }
        return builder.build();
    }

    protected <T> HttpResponse<T> getResponse(CompletableFuture<HttpResponse<T>> future) {
        HttpResponse<T> response = null;
        try {
            response = future.get(configuration.getResponseTimeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.error("response error. ", e);
        }
        return response;
    }

    protected <T> HttpResponse<T> actionWithRetryWhenFail(CompletableFuture<HttpResponse<T>> future) {
        int tryCount = 0;
        HttpResponse<T> response = null;
        while ((response == null) && tryCount < configuration.getRetryTime()) {
            LOGGER.debug("request send " + tryCount + " time");
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

    public Map<String, String> action(RouterRequest request) {
        long startTime = System.nanoTime();
        LOGGER.debug(OPERATION_NAME + "开始通信: " + request);

        var url = request.getUrl();
        var encoder = new QueryStringEncoder(url);
        var queries = request.getQueries();
        if (queries != null && !queries.isEmpty()) {
            queries.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    value.forEach(it -> encoder.addParam(key, it));
                }
            });
        }

        URI uri = null;
        try {
            uri = encoder.toUri();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        var builder = HttpRequest.newBuilder(uri);

        var header = request.getHeader();
        if (header != null && !header.isEmpty()) {
            var headers = header.getHeaders();
            headers.forEach((key, value) -> builder.header(key, String.join(";", value)));
        }

        var cookies = request.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            var cookie = buildCookies(cookies);
            LOGGER.debug("request Cookie: " + cookie);
            builder.header("Cookie", cookie);
        }

        var contentType = request.getContentType();
        if (contentType == null) {
            contentType = MediaType.ANY.info();
        }

        builder.header("Content-Type", contentType.toString());

        var xWwwFormUrlencodedBody = new StringBuilder();
        var parameters = request.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    for (var it: value) {
                        xWwwFormUrlencodedBody.append(key).append("=").append(it).append("&");
                    }
                }
            });
        }
        if (xWwwFormUrlencodedBody.length() > 0) {
            xWwwFormUrlencodedBody.deleteCharAt(xWwwFormUrlencodedBody.length() - 1);
        }

        if (contentType.toMediaType() == MediaType.APPLICATION_FORM_URLENCODED) {
            // form 表单
            var body = HttpRequest.BodyPublishers.ofString(xWwwFormUrlencodedBody.toString());
            var httpRequest = builder.method(request.getMethod().name(), body).build();
            return action(httpRequest, startTime);
        } else if (contentType.toMediaType() == MediaType.MULTIPART_FORM_DATA) {
            // multipart
            var multipart = ofMimeMultipartData(parameters, UUID.randomUUID().toString());
            var httpRequest = builder.method(request.getMethod().name(), multipart).build();
            return action(httpRequest, startTime);
        } else {
            if (request.getInputStreamBody() != null) {
                var body = HttpRequest.BodyPublishers.ofInputStream(request::getInputStreamBody);
                var httpRequest = builder.method(request.getMethod().name(), body).build();
                return action(httpRequest, startTime);
            } else if (request.getTextBody() != null) {
                var body = HttpRequest.BodyPublishers.ofString(request.getTextBody());
                var httpRequest = builder.method(request.getMethod().name(), body).build();
                return action(httpRequest, startTime);
            } else if (request.getFileBody() != null) {
                HttpRequest.BodyPublisher body = null;
                try {
                    body = HttpRequest.BodyPublishers.ofFile(request.getFileBody().toPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                var httpRequest = builder.method(request.getMethod().name(), body).build();
                return action(httpRequest, startTime);
            } else {
                var httpRequest = builder.method(request.getMethod().name(), null).build();
                return action(httpRequest, startTime);
            }
        }

    }

    protected Map<String, String> action(HttpRequest httpRequest, long startTime) {
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
            LOGGER.debug(OPERATION_NAME + "通讯完成，http 状态：" + HttpStatus.valueOf(response.statusCode()));
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
                    LOGGER.trace(OPERATION_NAME + "返回内容：" + result);
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

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientAction.class);
}
