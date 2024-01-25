/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.httpclient.form.FileFormDataParam;
import com.truthbean.debbie.httpclient.form.FormDataParam;
import com.truthbean.debbie.httpclient.form.TextFromDataParam;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.MultipartFile;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
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
        HttpClientProxy proxy = configuration.getProxy();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(proxy.getProxyHost(), proxy.getProxyPort());
        return ProxySelector.of(inetSocketAddress);
    }

    public Proxy createProxy() {
        HttpClientProxy proxy = configuration.getProxy();
        SocketAddress socketAddress = new InetSocketAddress(proxy.getProxyHost(), proxy.getProxyPort());
        return new Proxy(Proxy.Type.HTTP, socketAddress);
    }

    public String buildCookies(List<HttpCookie> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.stream().map((cookie) -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.joining(";"));
        }
        return "";
    }

    public HttpRequest.BodyPublisher ofMimeMultipartData(Map<String, List<Object>> params, String boundary) {
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
                            buildPart(byteArrays, key, path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (it instanceof File) {
                        try {
                            var path = ((File) it).toPath();
                            buildPart(byteArrays, key, path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (it instanceof MultipartFile) {
                        var file = (MultipartFile) it;
                        byteArrays.add(("\"" + key + "\"; filename=\"" + file.getFileName()
                                + "\"\r\nContent-Type: " + file.getContentType().toString() + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                        byteArrays.add(file.getContent());
                        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
                    } else {
                        byteArrays.add(("\"" + key + "\"\r\n\r\n" + it + "\r\n").getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        });
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

    public void buildForm(List<FormDataParam> params, String boundary, OutputStream output) {

        // Line separator required by multipart/form-data.
        final String CRLF = "\r\n";

        // params
        if (params != null && !params.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(output), true)) {
                // true = autoFlush, important!

                for (FormDataParam param : params) {
                    if (param instanceof TextFromDataParam) {
                        var text = (TextFromDataParam) param;
                        // normal param
                        writer.append("--").append(boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data;name=\"").append(text.getName()).append("\"").append(CRLF);

                        String charset = text.getCharset();
                        if (charset != null) {
                            writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
                        } else {
                            writer.append("Content-Type: text/plain").append(CRLF);
                        }
                        writer.append(CRLF);
                        writer.append(text.getValue()).append(CRLF).flush();
                    } else if (param instanceof FileFormDataParam) {
                        var file = (FileFormDataParam) param;
                        var binaryFile = file.getFile();
                        var mediaType = file.getFileType();
                        if (mediaType == null || mediaType.isAny()) {
                            String typeFromName = URLConnection.guessContentTypeFromName(binaryFile.getName());
                            mediaType = MediaTypeInfo.parse(typeFromName);
                        }
                        // 二次判断
                        if (mediaType == null || mediaType.isAny()) {
                            mediaType = MediaType.APPLICATION_OCTET_STREAM.info();
                        }
                        var charset = mediaType.charset(Charset.defaultCharset());
                        writer.append("--").append(boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\"").append(file.getName()).append("\"; filename=\"").append(binaryFile.getName()).append("\"")
                                .append(CRLF);
                        writer.append("Content-Type: ").append(mediaType.toString()).append(CRLF);
                        if (mediaType.isText()) {
                            // text file
                            try (var fis = new FileInputStream(binaryFile);
                                 var isr = new InputStreamReader(fis, charset);
                                 BufferedReader reader = new BufferedReader(isr)) {
                                for (String line; (line = reader.readLine()) != null; ) {
                                    writer.append(line).append(CRLF);
                                }
                            }
                        } else {
                            // binaryFile file
                            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                            writer.append(CRLF).flush();
                            try (InputStream input = new FileInputStream(binaryFile)) {
                                byte[] buffer = new byte[4096];
                                for (int length = 0; (length = input.read(buffer)) > -1; ) {
                                    output.write(buffer, 0, length);
                                }
                                // Important! Output cannot be closed.
                                // Close of writer will close output as well.
                                output.flush();
                            }
                        }
                        writer.append(CRLF).flush();
                    }
                }

                // CRLF is important! It indicates end of binary boundary.
                writer.append(CRLF).flush();
                // End of multipart/form-data.
                writer.append("--").append(boundary).append("--").append(CRLF).flush();

            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    private void buildPart(ArrayList<byte[]> byteArrays, String key, Path path) throws IOException {
        String mimeType = Files.probeContentType(path);
        byteArrays.add(("\"" + key + "\"; filename=\"" + path.getFileName()
                + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(path));
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);
}
