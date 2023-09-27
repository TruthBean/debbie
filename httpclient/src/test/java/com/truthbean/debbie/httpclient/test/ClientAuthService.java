package com.truthbean.debbie.httpclient.test;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.httpclient.HttpClientHandler;
import com.truthbean.debbie.httpclient.HttpClientProperties;
import com.truthbean.debbie.httpclient.HttpClientResponse;
import com.truthbean.debbie.httpclient.HttpResponseType;
import com.truthbean.debbie.httpclient.form.FormDataParam;
import com.truthbean.debbie.mvc.request.HttpMethod;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 1.0.1
 * Created on 2021-05-24 12:13
 */
public class ClientAuthService {
    public static final String ak = "5irg3JG9oyFizRlDP7j9YCrKoMGtjXuP";
    public static final String sk = "5irg3JG9oyFizRlDP7j9YCrKoMGtjXuP";

    private static final String bashUrl = "http://192.168.1.111:12002";

    public static String calHash(String url, String method, String query, byte[] body, Long time) {
        String urlHash = HashUtils.md5WithBase64(url);
        LOGGER.debug("urlHash: " + urlHash);
        String methodHash = HashUtils.md5WithBase64(method.toUpperCase());
        LOGGER.debug("methodHash: " + methodHash);
        String queryHash = HashUtils.md5WithBase64(query);
        LOGGER.debug("queryHash: " + queryHash);
        String bodyHash = HashUtils.md5WithBase64(body);
        LOGGER.debug("bodyHash: " + bodyHash);
        return HashUtils.md5WithBase64(sk.substring(0, 8) + urlHash
                + sk.substring(8, 16) + methodHash
                + sk.substring(16, 24) + queryHash
                + sk.substring(24, 32) + bodyHash + HashUtils.md5WithBase64(time.toString()));
    }

    public static String http(String url, HttpMethod method, Map<String, String> queries, Map<String, String> headers, byte[] body, Long time) {
        String authAk = ak;
        String queryString = null;
        if (queries != null && !queries.isEmpty()) {
            StringBuilder queryStringBuilder = new StringBuilder();
            StringUtils.joining(queries, "&", "=", queryStringBuilder);
            queryString = queryStringBuilder.toString();
        }
        String authHash = calHash(url, method.name(), queryString, body, time);
        String hashTime = time.toString();
        headers.put("Auth-Ak", authAk);
        headers.put("Auth-Hash", authHash);
        headers.put("Hash-Time", hashTime);
        HttpClientHandler httpClientHandler = new HttpClientHandler(new HttpClientProperties().loadConfiguration());
        HttpClientResponse response = httpClientHandler.doHttp(bashUrl + url, method, queries, headers, null, body, HttpResponseType.STRING);
        return (String) httpClientHandler.getBody(response);
    }

    public static String form(String url, HttpMethod method, Map<String, String> queries, Map<String, String> headers,
                              List<FormDataParam> params, Long time) {
        String authAk = ak;
        String queryString = null;
        if (queries != null && !queries.isEmpty()) {
            StringBuilder queryStringBuilder = new StringBuilder();
            StringUtils.joining(queries, "&", "=", queryStringBuilder);
            queryString = queryStringBuilder.toString();
        }
        String boundary = Long.toHexString(System.currentTimeMillis());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpClientHandler httpClientHandler = new HttpClientHandler(new HttpClientProperties().loadConfiguration());
        httpClientHandler.buildForm(params, boundary, output);

        String contentType = "multipart/form-data; boundary=" + boundary;
        headers.put("Content-Type", contentType);

        byte[] body = output.toByteArray();
        String authHash = calHash(url, method.name(), queryString, body, time);
        String hashTime = time.toString();
        headers.put("Auth-Ak", authAk);
        headers.put("Auth-Hash", authHash);
        headers.put("Hash-Time", hashTime);
        HttpClientResponse response = httpClientHandler.doHttp(bashUrl + url, method, queries, headers, null, body, HttpResponseType.STRING);
        return (String) httpClientHandler.getBody(response);
    }

    public static String httpNoAuth(String url, HttpMethod method, Map<String, String> queries, Map<String, String> headers, byte[] body) {
        HttpClientHandler httpClientHandler = new HttpClientHandler(new HttpClientProperties().loadConfiguration());
        HttpClientResponse response = httpClientHandler.doHttp(bashUrl + url, method, queries, headers, null, body, HttpResponseType.STRING);
        return (String) httpClientHandler.getBody(response);
    }

    public static byte[] downloadNoAuth(String url, HttpMethod method, Map<String, String> queries, Map<String, String> headers, byte[] body) {
        HttpClientHandler httpClientHandler = new HttpClientHandler(new HttpClientProperties().loadConfiguration());
        HttpClientResponse response = httpClientHandler.doHttp(bashUrl + url, method, queries, headers, null, body, HttpResponseType.BYTES);
        return (byte[]) httpClientHandler.getBody(response);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthService.class);
}