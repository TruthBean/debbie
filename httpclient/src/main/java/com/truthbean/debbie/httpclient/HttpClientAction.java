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

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.lang.TypeNotSupportedException;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.net.uri.QueryStringEncoder;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientAction extends HttpHandler {

    private static final HttpClient.Builder HTTP_CLIENT_BUILDER = HttpClient.newBuilder();

    private final HttpClient httpClient;
    private final HttpClientConfiguration configuration;

    public HttpClientAction(final HttpClientConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
        this.httpClient = createHttpClient();
    }

    protected HttpClient createHttpClient() {
        final HttpClient.Builder builder = HTTP_CLIENT_BUILDER;
        if (configuration.useProxy()) {
            builder.proxy(createProxySelector());
        }
        if (configuration.needAuth()) {
            builder.authenticator(basicAuth());
        }
        if (configuration.isInsecure()) {
            builder.sslContext(createSslContext());
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        }
        return builder.build();
    }

    @SuppressWarnings("rawtypes")
    protected HttpResponse getResponse(final CompletableFuture<HttpResponse> future) {
        HttpResponse response = null;
        try {
            response = future.get(configuration.getResponseTimeout(), TimeUnit.MILLISECONDS);
        } catch (final Exception e) {
            String message = e.getMessage();
            if (message != null && message.startsWith("java.net.ConnectException:")) {
                throw new HttpClientException(message);
            }
            LOGGER.error("response error. ", e);
        }
        return response;
    }

    @SuppressWarnings("rawtypes")
    protected HttpResponse actionWithRetryWhenFail(CompletableFuture<HttpResponse> future) {
        int tryCount = 0;
        HttpResponse response = null;
        while ((response == null) && tryCount < configuration.getRetryTime()) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("request send " + tryCount + " time");
            tryCount++;
            // retry the HttpRequest
            future = future.copy();
            try {
                response = future.get(configuration.getResponseTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                if (tryCount >= configuration.getRetryTime()) {
                    String message = e.getMessage();
                    if (message != null && message.startsWith("java.net.ConnectException:")) {
                        throw new HttpClientException(e);
                    }
                }
                LOGGER.error("response error. ", e);
            }
            if (response != null) {
                try {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug(OPERATION_NAME + "通讯完成，返回码：" + response.statusCode());
                } catch (final Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
        return response;
    }

    @SuppressWarnings("rawtypes")
    public HttpClientResponse action(final RouterRequest request, final MediaTypeInfo responseTypeInfo) {
        HttpResponseType responseType;
        if (responseTypeInfo.isText()) {
            responseType = HttpResponseType.STRING;
        } else {
            responseType = HttpResponseType.BYTES;
        }
        final long startTime = System.nanoTime();
        LOGGER.debug(() -> OPERATION_NAME + "开始通信: " + request);

        final var url = request.getUrl();
        final var encoder = new QueryStringEncoder(url);
        final var queries = request.getQueries();
        if (queries != null && !queries.isEmpty()) {
            queries.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    value.forEach(it -> encoder.addParam(key, it));
                }
            });
        }

        URI uri;
        try {
            uri = encoder.toUri();
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        LOGGER.trace(() -> "request uri: " + uri);
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        final var builder = HttpRequest.newBuilder(uri);

        final var header = request.getHeader();
        if (header != null && !header.isEmpty()) {
            final var headers = header.getHeaders();
            headers.forEach((key, value) -> builder.header(key, String.join(";", value)));
        }

        final var cookies = request.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            final var cookie = buildCookies(cookies);
            LOGGER.debug(() -> "request Cookie: " + cookie);
            builder.header("Cookie", cookie);
        }

        var contentType = request.getContentType();
        if (contentType == null) {
            contentType = MediaType.ANY.info();
        }

        if (!contentType.equals(MediaType.MULTIPART_FORM_DATA.info())) {
            builder.header("Content-Type", contentType.toString());
        }

        final var xWwwFormUrlencodedBody = new StringBuilder();
        final var parameters = request.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    for (final var it: value) {
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
            final var body = HttpRequest.BodyPublishers.ofString(xWwwFormUrlencodedBody.toString());
            final var httpRequest = builder.method(request.getMethod().name(), body).build();
            return action(httpRequest, startTime, responseType);
        } else if (contentType.toMediaType() == MediaType.MULTIPART_FORM_DATA) {
            // multipart
            var boundary = UUID.randomUUID().toString();
            builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            final var multipart = ofMimeMultipartData(parameters, boundary);
            final var httpRequest = builder.method(request.getMethod().name(), multipart).build();
            return action(httpRequest, startTime, responseType);
        } else {
            if (request.getInputStreamBody() != null) {
                final var body = HttpRequest.BodyPublishers.ofInputStream(request::getInputStreamBody);
                final var httpRequest = builder.method(request.getMethod().name(), body).build();
                return action(httpRequest, startTime, responseType);
            } else if (request.getTextBody() != null) {
                final var body = HttpRequest.BodyPublishers.ofString(request.getTextBody());
                final var httpRequest = builder.method(request.getMethod().name(), body).build();
                return action(httpRequest, startTime, responseType);
            } else if (request.getFileBody() != null) {
                HttpRequest.BodyPublisher body = null;
                try {
                    body = HttpRequest.BodyPublishers.ofFile(request.getFileBody().toPath());
                } catch (final FileNotFoundException e) {
                    e.printStackTrace();
                }
                final var httpRequest = builder.method(request.getMethod().name(), body).build();
                return action(httpRequest, startTime, responseType);
            } else {
                final var httpRequest = builder.method(request.getMethod().name(), HttpRequest.BodyPublishers.noBody()).build();
                return action(httpRequest, startTime, responseType);
            }
        }
    }

    public void buildRequest(final HttpRequest.Builder builder, final HttpMethod httpMethod, final HttpRequest.BodyPublisher bodyPublisher) {
        switch (httpMethod) {
            case GET:
                builder.GET();
                break;
            case POST:
                builder.POST(bodyPublisher);
                break;
            case PUT:
                builder.PUT(bodyPublisher);
                break;
            case DELETE:
                builder.DELETE();
                break;
            default:
                builder.method(httpMethod.name(), bodyPublisher);
                break;
        }
    }

    @SuppressWarnings("rawtypes")
    private <T> CompletableFuture sendAsync(final HttpRequest httpRequest, final Class<T> type) {
        CompletableFuture future = null;
        if (type == String.class) {
            future = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        } else if (type == byte[].class) {
            future = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        } else if (type == Stream.class) {
            future = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines());
        } else if (type == InputStream.class) {
            future = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } else {
            throw new TypeNotSupportedException("Only support String, byte[], Stream<String>, InputStream");
        }
        return future;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected HttpClientResponse action(final HttpRequest httpRequest, final long startTime, final HttpResponseType type) {
        HttpClientResponse result = null;
        CompletableFuture<HttpResponse> future;
        switch (type) {
            case BYTES:
                future = sendAsync(httpRequest, byte[].class);
                break;
            case INPUTSTREAM:
                future = sendAsync(httpRequest, InputStream.class);
                break;
            case STREAM_STRING:
                future = sendAsync(httpRequest, Stream.class);
                break;
            default:
                future = sendAsync(httpRequest, String.class);
                break;
        }
        HttpResponse<String> response;
        if (configuration.retry()) {
            response = actionWithRetryWhenFail(future);
        } else {
            response = getResponse(future);
        }

        if (response != null) {
            result = new HttpClientResponse();

            final int code = response.statusCode();
            LOGGER.debug(() -> OPERATION_NAME + "通讯完成，返回码：" + code + "，http 状态：" + HttpStatus.valueOf(code));

            result.setCode(code);
            result.setSslSession(response.sslSession().orElse(null));
            result.setVersion(response.version());

            final HttpHeaders responseHeaders = response.headers();
            result.setHeaders(responseHeaders.map());
            LOGGER.debug(() -> "whether it's compressed，VALUE is " + responseHeaders.allValues("Content-Encoding"));

            final Object responseBody = response.body();
            if (responseBody != null) {
                try {
                    result.setBody(responseBody);
                } catch (final Exception e) {
                    LOGGER.error("通讯成功，解析返回值异常", e);
                }
                if (LOGGER.isTraceEnabled())
                    LOGGER.trace(OPERATION_NAME + "返回内容：" + result);
            } else {
                LOGGER.error("通讯异常");
            }
        } else {
            LOGGER.error("通讯失败");
        }

        final long endTime = System.nanoTime();
        LOGGER.debug(() -> OPERATION_NAME + "共计耗时:" + ((endTime - startTime) / 1000000.0) + "ms");
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientAction.class);
}
