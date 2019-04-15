package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.io.StreamHelper;
import com.truthbean.debbie.mvc.request.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

    public String get(String url, List<HttpCookie> cookies) {
        return get(url, cookies, null, null, null);
    }

    public String get(String url, List<HttpCookie> cookies, Map<String, String> headers,
                      MediaType contentType, byte[] body) {
        HttpURLConnection connection = prepare(url, HttpMethod.GET, cookies, headers, contentType, body);
        if (connection != null) {
            return action(connection);
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
            return action(connection);
        }
        return null;
    }

    private HttpURLConnection prepare(String url, HttpMethod method,
                                      List<HttpCookie> cookies, Map<String, String> headers,
                                      MediaType contentType, byte[] body) {
        LOGGER.info("connect to: " + url);
        try {
            HttpURLConnection connection;
            if (url.startsWith("https")) {
                connection = createHttpsUrlConnection(url);
            } else {
                connection = (HttpURLConnection) createUrlConnection(url);
            }
            connection.setRequestMethod(method.name());
            if (contentType != null) {
                connection.setRequestProperty("Content-Type", contentType.getValue());
            }
            if (cookies != null && !cookies.isEmpty()) {
                var cookie = buildCookies(cookies);
                LOGGER.debug("request Cookie: " + cookie);
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
            e.printStackTrace();
        }
        return null;
    }

    protected String action(HttpURLConnection connection) {
        int status;
        try {
            status = connection.getResponseCode();
        } catch (IOException e) {
            LOGGER.warn("IOException send message", e);
            return null;
        }

        if (status / 100 == 5) {
            LOGGER.info("remote service is unavailable (status " + status + ")");
            return null;
        } else {
            String responseBody;
            if (status != 200) {
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


    public URLConnection createUrlConnection(String urlSpec) {
        try {
            var url = new URL(urlSpec);
            URLConnection urlConnection;

            var configuration = getConfiguration();
            if (configuration.useProxy()) {
                urlConnection = url.openConnection(createProxy());
            } else {
                urlConnection = url.openConnection();
            }

            urlConnection.setConnectTimeout(configuration.getConnectTimeout());
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setDoOutput(true);
            urlConnection.setReadTimeout(configuration.getReadTimeout());

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
            sc.init(null, new TrustManager[]{SSL_HANDLER}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(SSL_HANDLER);

            return (HttpsURLConnection) createUrlConnection(urlSpec);

        } catch (Exception e) {
            LOGGER.debug("https config ssl failure: " + e.getMessage());
        }

        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionHandler.class);

}
