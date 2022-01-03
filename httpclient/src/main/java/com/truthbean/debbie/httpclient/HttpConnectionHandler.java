/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.httpclient.form.FormDataParam;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.response.BaseRouterResponse;
import com.truthbean.debbie.mvc.response.HttpStatus;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpConnectionHandler extends HttpHandler {

    public HttpConnectionHandler(HttpClientConfiguration configuration) {
        super(configuration);
    }

    public String get(String url) {
        return get(url, null, null, null, null);
    }

    public void download(String url, File file) {
        HttpURLConnection connection = prepare(url, HttpMethod.GET);
        int status;
        try {
            status = connection.getResponseCode();
        } catch (IOException e) {
            LOGGER.warn("IOException send message", e);
            return;
        }

        LOGGER.debug(() -> "remote service response status: " + status);

        if (status / 100 == 5) {
            LOGGER.info("remote service is unavailable (status " + status + ")");
        } else {
            if (status / 100 != 2) {
                try {
                    String responseBody = StreamHelper.getAndClose(connection.getErrorStream());
                    LOGGER.error("Plain post error response: " + responseBody);
                } catch (IOException e) {
                    LOGGER.warn("Exception reading response", e);
                }
            } else {
                try {
                    StreamHelper.copyLarge(connection.getInputStream(), new FileOutputStream(file));
                } catch (IOException e) {
                    LOGGER.warn("Exception reading response", e);
                }

            }
        }
    }

    public String get(String url, List<HttpCookie> cookies) {
        return get(url, cookies, null, null, null);
    }

    public String get(String url, List<HttpCookie> cookies, Map<String, String> headers,
                      MediaType contentType, byte[] body) {
        HttpURLConnection connection = prepare(url, HttpMethod.GET, cookies, headers, contentType, body);
        if (connection != null) {
            return getResponse(connection);
        }
        return null;
    }

    public String post(String url, String body, MediaType contentType) {
        return post(url, null, null, contentType, body.getBytes());
    }

    public String post(String url, List<HttpCookie> cookies, Map<String, String> headers,
                       MediaType contentType, byte[] body) {
        HttpURLConnection connection = prepare(url, HttpMethod.POST, cookies, headers, contentType, body);
        if (connection != null) {
            return getResponse(connection);
        }
        return null;
    }

    public String delete(String url) {
        return delete(url, null, null, null, null);
    }

    public String delete(String url, List<HttpCookie> cookies, Map<String, String> headers,
                         MediaType contentType, byte[] body) {
        HttpURLConnection connection = prepare(url, HttpMethod.DELETE, cookies, headers, contentType, body);
        if (connection != null) {
            return getResponse(connection);
        }
        return null;
    }

    public String put(String url, String body, MediaType contentType) {
        return put(url, null, null, contentType, body.getBytes());
    }

    public String put(String url, List<HttpCookie> cookies, Map<String, String> headers,
                       MediaType contentType, byte[] body) {
        HttpURLConnection connection = prepare(url, HttpMethod.POST, cookies, headers, contentType, body);
        if (connection != null) {
            return getResponse(connection);
        }
        return null;
    }

    public String form(String url, List<FormDataParam> params) {
        HttpURLConnection connection = prepare(url, HttpMethod.POST);
        if (connection == null) {
            return null;
        }

        // Just generate some unique random VALUE.
        String boundary = Long.toHexString(System.currentTimeMillis());
        String contentType = "multipart/form-data; boundary=" + boundary;
        // Line separator required by multipart/form-data.
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType);

        try {
            OutputStream output = connection.getOutputStream();
            buildForm(params, boundary, output);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return getResponse(connection);
    }

    public BaseRouterResponse request(String url, HttpMethod method,
                                      List<HttpCookie> cookies, Map<String, String> headers,
                                      String contentType, byte[] body) {
        HttpURLConnection connection = prepare(url, method, cookies, headers, contentType, body);
        if (connection != null) {
            return getResponseInfo(connection);
        }
        return null;
    }

    private HttpURLConnection prepare(String url, HttpMethod method) {
        return prepare(url, method, null, null, (String) null, null);
    }

    private HttpURLConnection prepare(String url, HttpMethod method,
                                      List<HttpCookie> cookies, Map<String, String> headers,
                                      MediaType contentType, byte[] body) {
        String contentTypeValue = null;
        if (contentType != null) {
            contentTypeValue = contentType.getValue();
        }
        return prepare(url, method, cookies, headers, contentTypeValue, body);
    }

    private HttpURLConnection prepare(String url, HttpMethod method,
                                      List<HttpCookie> cookies, Map<String, String> headers,
                                      String contentType, byte[] body) {
        LOGGER.info("connect to: " + url);
        try {
            HttpURLConnection connection;
            if (url.startsWith("https")) {
                connection = createHttpsUrlConnection(url);
            } else {
                connection = (HttpURLConnection) createUrlConnection(url);
            }
            connection.setRequestMethod(method.name());

            final HttpClientConfiguration configuration = getConfiguration();
            if (configuration != null) {
                if (configuration.needAuth()) {
                    connection.setAuthenticator(super.basicAuth());
                }
            }

            if (contentType != null) {
                connection.setRequestProperty("Content-Type", contentType);
            }
            if (cookies != null && !cookies.isEmpty()) {
                var cookie = buildCookies(cookies);
                LOGGER.debug(() -> "request Cookie: " + cookie);
                connection.setRequestProperty("Cookie", cookie);
            }
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(connection::setRequestProperty);
            }

            if (body != null && body.length > 0) {
                connection.setFixedLengthStreamingMode(body.length);
                OutputStream out = connection.getOutputStream();
                try {
                    out.write(body);
                } finally {
                    StreamHelper.close(out);
                }
            }

            return connection;
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    protected String getResponse(HttpURLConnection connection) {
        int status;
        try {
            status = connection.getResponseCode();
        } catch (IOException e) {
            LOGGER.warn("IOException send message", e);
            return null;
        }

        LOGGER.debug(() -> "remote service response status: " + status);

        if (status / 100 == 5) {
            LOGGER.info("remote service is unavailable (status " + status + ")");
            return null;
        } else {
            String responseBody;
            if (status / 100 != 2) {
                try {
                    responseBody = StreamHelper.getAndClose(connection.getErrorStream());
                    LOGGER.error("Plain post error response: " + responseBody);
                } catch (IOException e) {
                    LOGGER.warn("Exception reading response", e);
                }
            } else {
                try {
                    responseBody = StreamHelper.getAndClose(connection.getInputStream());
                } catch (IOException e) {
                    LOGGER.warn("Exception reading response", e);
                    return null;
                }

                return responseBody;
            }
        }
        return null;
    }

    protected BaseRouterResponse getResponseInfo(HttpURLConnection connection) {
        int status;
        try {
            status = connection.getResponseCode();
        } catch (IOException e) {
            LOGGER.warn("IOException send message", e);
            return null;
        }

        BaseRouterResponse response = new BaseRouterResponse();
        response.setStatus(HttpStatus.ofOrNew(status, ""));

        String responseBody = null;
        try {
            responseBody = StreamHelper.getAndClose(connection.getInputStream());
        } catch (IOException e) {
            LOGGER.warn("Exception reading response", e);
        }
        if (responseBody == null) {
            try {
                responseBody = StreamHelper.getAndClose(connection.getErrorStream());
                LOGGER.error("Plain post error response: " + responseBody);
            } catch (IOException e) {
                LOGGER.warn("Exception reading response", e);
            }
        }
        response.setContent(responseBody);
        return null;
    }

    public URLConnection createUrlConnection(String urlSpec) {
        try {
            var url = new URL(urlSpec);
            URLConnection urlConnection;

            var configuration = getConfiguration();
            if (configuration.useProxy()) {
                HttpClientProxy proxy = configuration.getProxy();
                LOGGER.trace("user proxy");
                urlConnection = url.openConnection(createProxy());
                if (proxy.needAuth()) {
                    String encoded = Base64.getEncoder().encodeToString((proxy.getUser() + ":" + proxy.getPassword()).getBytes());
                    urlConnection.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
                }
            } else {
                urlConnection = url.openConnection();
            }

            if (configuration.getConnectTimeout() > 0) {
                urlConnection.setConnectTimeout(configuration.getConnectTimeout());
            }
            if (configuration.getReadTimeout() > 0) {
                urlConnection.setReadTimeout(configuration.getReadTimeout());
            }
            if (configuration.isUseCache()) {
                urlConnection.setUseCaches(true);
            }
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setDoOutput(true);

            if (configuration.needAuth()) {
                var basicAuth = (configuration.getAuthUser() + ":" + configuration.getAuthPassword()).getBytes();
                var authBase64 = Base64.getEncoder().encodeToString(basicAuth);
                urlConnection.setRequestProperty("Authorization", "Basic " + authBase64);
            }

            return urlConnection;

        } catch (IOException e) {
            LOGGER.error("create connection error. ", e);
        }

        return null;
    }

    public HttpsURLConnection createHttpsUrlConnection(String urlSpec) {
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{SSL_HANDLER}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(SSL_HANDLER);

            return (HttpsURLConnection) createUrlConnection(urlSpec);

        } catch (Exception e) {
            LOGGER.debug(() -> "https config ssl failure: " + e.getMessage());
        }

        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionHandler.class);

}
