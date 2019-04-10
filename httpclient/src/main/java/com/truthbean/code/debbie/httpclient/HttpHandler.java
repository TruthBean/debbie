package com.truthbean.code.debbie.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpHandler {

    private HttpClientConfiguration configuration;

    public HttpHandler(HttpClientConfiguration configuration) {
        this.configuration = configuration;
    }

    static final String OPERATION_NAME = "【HTTP操作】";

    public HttpClientConfiguration getConfiguration() {
        return configuration;
    }

    public HostnameVerifier hostnameVerifier() {
        return (s, sslSession) -> true;
    }

    public SSLContext createSSLContext() {
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
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public boolean verify(String paramString, SSLSession paramSSLSession) {
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

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);
}
