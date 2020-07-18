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

import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpHandler {

    private final HttpClientConfiguration configuration;

    public HttpHandler(HttpClientConfiguration configuration) {
        this.configuration = configuration;
    }

    static final String OPERATION_NAME = "【HTTP操作】";

    public HttpClientConfiguration getConfiguration() {
        return configuration;
    }

    public SSLContext createSslContext() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{SSL_HANDLER}, new SecureRandom());
            return sc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static final SslHandler SSL_HANDLER = new SslHandler(null);

    static class SslHandler implements X509TrustManager, HostnameVerifier {
        private SslHandler(Object o) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public boolean verify(String paramString, SSLSession paramSslSession) {
            return true;
        }
    }

    public Authenticator basicAuth() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configuration.getAuthUser(),
                        configuration.getAuthPassword().toCharArray());
            }
        };
    }

    public ProxySelector createProxySelector() {
        InetSocketAddress inetSocketAddress =
                new InetSocketAddress(configuration.getProxyHost(), configuration.getProxyPort());
        return ProxySelector.of(inetSocketAddress);
    }

    public Proxy createProxy() {
        SocketAddress socketAddress =
                new InetSocketAddress(configuration.getProxyHost(), configuration.getProxyPort());
        return new Proxy(Proxy.Type.HTTP, socketAddress);
    }

    public String buildCookies(List<HttpCookie> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.stream().map((cookie) -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.joining(";"));
        }
        return "";
    }

    @SuppressWarnings("rawtypes")
    public HttpRequest.BodyPublisher ofMimeMultipartData(Map<String, List> params, String boundary) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);

        params.forEach((key, value) -> {
            byteArrays.add(separator);

            if (value != null && !value.isEmpty()) {
                for (var it : value) {
                    if (it instanceof Path) {
                        try {
                            var path = (Path) it;
                            String mimeType = Files.probeContentType(path);
                            byteArrays.add(("\"" + key + "\"; filename=\"" + path.getFileName()
                                    + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                            byteArrays.add(Files.readAllBytes(path));
                            byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        byteArrays.add(("\"" + key + "\"\r\n\r\n" + it + "\r\n").getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        });
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
