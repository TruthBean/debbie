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

import com.truthbean.debbie.httpclient.form.FileFormDataParam;
import com.truthbean.debbie.httpclient.form.FormDataParamName;
import com.truthbean.debbie.httpclient.form.TextFromDataParam;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
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

    public String form(String url, List<FormDataParamName> params) {
        HttpURLConnection connection = prepare(url, HttpMethod.POST);
        if (connection == null) {
            return null;
        }

        // Just generate some unique random value.
        String boundary = Long.toHexString(System.currentTimeMillis());
        String contentType = "multipart/form-data; boundary=" + boundary;
        // Line separator required by multipart/form-data.
        String CRLF = "\n";
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType);

        // params
        if (params != null && !params.isEmpty()) {
            PrintWriter writer = null;
            try {
                OutputStream output = connection.getOutputStream();
                // true = autoFlush, important!
                writer = new PrintWriter(new OutputStreamWriter(output), true);

                for (FormDataParamName param : params) {
                    if (param instanceof TextFromDataParam) {
                        var text = (TextFromDataParam) param;
                        // normal param
                        writer.append("--" + boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data;name=\"" + text.getName() + "\"").append(CRLF);

                        String charset = text.getCharset();
                        if (charset != null) {
                            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
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
                        var charset = mediaType.charset(Charset.defaultCharset());
                        writer.append("--").append(boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\"").append(file.getName()).append("\"; filename=\"").append(binaryFile.getName()).append("\"")
                                .append(CRLF);
                        writer.append("Content-Type: ").append(mediaType.toString()).append(CRLF);
                        if (mediaType.isText()) {
                            // text file
                            BufferedReader reader = null;
                            try {
                                reader = new BufferedReader(new InputStreamReader(new FileInputStream(binaryFile), charset));
                                for (String line; (line = reader.readLine()) != null;) {
                                    writer.append(line).append(CRLF);
                                }
                            } finally {
                                if (reader != null)
                                    try { reader.close(); }
                                    catch (IOException logOrIgnore) {}
                            }
                        } else {
                            // binaryFile file
                            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                            writer.append(CRLF).flush();
                            InputStream input = null;
                            try {
                                input = new FileInputStream(binaryFile);
                                byte[] buffer = new byte[1024];
                                for (int length = 0; (length = input.read(buffer)) > 0;) {
                                    output.write(buffer, 0, length);
                                }
                                // Important! Output cannot be closed.
                                // Close of writer will close output as well.
                                output.flush();
                            } finally {
                                if (input != null)
                                    try { input.close(); }
                                    catch (IOException logOrIgnore) {}
                            }
                        }
                        writer.flush();
                    }
                }

                // CRLF is important! It indicates end of binary boundary.
                writer.append(CRLF).flush();
                // End of multipart/form-data.
                writer.append("--").append(boundary).append("--").append(CRLF).flush();

            } catch (IOException e) {
                LOGGER.error("", e);
            } finally {
                if (writer != null) writer.close();
            }
        }

        return getResponse(connection);
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


    public URLConnection createUrlConnection(String urlSpec) {
        try {
            var url = new URL(urlSpec);
            URLConnection urlConnection;

            var configuration = getConfiguration();
            if (configuration.useProxy()) {
                LOGGER.trace("user proxy");
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
