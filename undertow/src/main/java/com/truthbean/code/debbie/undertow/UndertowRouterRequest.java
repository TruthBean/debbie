package com.truthbean.code.debbie.undertow;

import com.truthbean.code.debbie.core.io.FileNameUtils;
import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.io.MultipartFile;
import com.truthbean.code.debbie.core.io.StreamHelper;
import com.truthbean.code.debbie.core.net.url.UriUtils;
import com.truthbean.code.debbie.mvc.RouterSession;
import com.truthbean.code.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.code.debbie.mvc.request.HttpMethod;
import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.url.RouterPathAttribute;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 11:01.
 */
public class UndertowRouterRequest implements RouterRequest {
    private final HttpServerExchange exchange;

    private final Map<String, List<String>> headers = new HashMap<>();

    private final DefaultRouterRequest routerRequestCache = new DefaultRouterRequest();

    public UndertowRouterRequest(HttpServerExchange exchange) {
        this.exchange = exchange;

        routerRequestCache.setMethod(HttpMethod.valueOf(exchange.getRequestMethod().toString()));
        routerRequestCache.setUrl(exchange.getRequestURI());

        setPathAttributes();
        setHeaders();

        List<String> contentType = this.headers.get("Content-Type");
        List<String> responseType = this.headers.get("Response-Type");

        setCookies();

        setParams(contentType);
        setQueries();

    }

    private void setPathAttributes() {
        List<RouterPathAttribute> result = new ArrayList<>();
        var regex = "{\\s}";
        // todo
        routerRequestCache.setPathAttributes(result);
    }

    private void setHeaders() {
        HeaderMap headerMap = exchange.getRequestHeaders();
        Map<String, List<String>> headers = new HashMap<>();
        if (headerMap != null) {
            // head
            Collection<HttpString> headerNames = headerMap.getHeaderNames();
            headerNames.forEach(httpString -> headers.put(httpString.toString(), headerMap.get(httpString)));

            var contentType = getMediaTypeFromHeaders(headerMap, "Content-Type");
            routerRequestCache.setContentType(contentType);
            var responseType = getMediaTypeFromHeaders(headerMap, "Response-Type");
            routerRequestCache.setResponseType(responseType);
        }
        this.headers.putAll(headers);
        routerRequestCache.setHeaders(headers);
    }

    private MediaType getMediaTypeFromHeaders(HeaderMap headerMap, String name) {
        HeaderValues headerValues = headerMap.get(name);
        MediaType type;
        if (headerValues != null && headerValues.element() != null) {
            type = MediaType.of(headerValues.element());
        } else {
            String ext = FileNameUtils.getExtension(getUrl());
            if (ext == null || "".equals(ext.trim())) {
                type = MediaType.ANY;
            } else {
                type = UriUtils.getTypeByUriExt(ext);
            }
        }
        return type;
    }

    private void setCookies() {
        var requestCookies = exchange.getRequestCookies();
        var cookies = requestCookies.values();
        List<HttpCookie> result = new ArrayList<>();

        if (!cookies.isEmpty()) {
            for (var cookie : cookies) {
                result.add(new UndertowRouterCookie(cookie).getHttpCookie());
            }
        }
        routerRequestCache.setCookies(result);
    }

    public MediaType getMediaType(List<String> type) {
        if (type != null && !type.contains(MediaType.APPLICATION_FORM_URLENCODED.getValue())
                && !type.contains(MediaType.MULTIPART_FORM_DATA.getValue())) {
            if (type.contains(MediaType.TEXT_PLAIN.getValue())) {
                return MediaType.TEXT_PLAIN;
            } else if (type.contains(MediaType.APPLICATION_JSON.getValue())) {
                return MediaType.APPLICATION_JSON;
            } else if (type.contains(MediaType.APPLICATION_XML.getValue())) {
                return MediaType.APPLICATION_XML;
            } else if (type.contains(MediaType.APPLICATION_JAVASCRIPT.getValue())) {
                return MediaType.APPLICATION_JAVASCRIPT;
            } else if (type.contains(MediaType.TEXT_HTML.getValue())) {
                return MediaType.TEXT_HTML;
            } else {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
        } else {
            return MediaType.ANY;
        }
    }

    public void setParams(List<String> contentType) {
        Map<String, List> parameters = new HashMap<>();
        exchange.getPathParameters().forEach((k, v) -> parameters.put(k, new ArrayList<>(v)));

        if (contentType != null && !contentType.isEmpty()) {
            if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED.getValue()) || contentType.contains(MediaType.MULTIPART_FORM_DATA.getValue())) {
                parameters.putAll(getFormData());
            }
        }

        parameters.putAll(getFormData());
        routerRequestCache.setParameters(parameters);
    }

    private Map<String, List> getFormData() {
        Map<String, List> parameters = new HashMap<>();
        try {
            FormParserFactory.Builder builder = FormParserFactory.builder();

            final FormDataParser formDataParser = builder.build().createParser(exchange);
            if (formDataParser != null) {
                exchange.startBlocking();
                FormData formData = formDataParser.parseBlocking();

                for (String data : formData) {
                    List dataValue = new ArrayList();
                    for (FormData.FormValue formValue : formData.get(data)) {
                        if (formValue.isFileItem()) {
                            FormData.FileItem fileItem = formValue.getFileItem();
                            MultipartFile file = new MultipartFile();
                            file.setContent(StreamHelper.toByteArray(fileItem.getInputStream()));
                            file.setFileName(formValue.getFileName());

                            dataValue.add(file);
                        } else {
                            dataValue.add(formValue.getValue());
                        }
                    }
                    parameters.put(data, dataValue);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
        return parameters;
    }

    private void setQueries() {
        Map<String, List<String>> queries = new HashMap<>();

        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
        if (!queryParameters.isEmpty()) {
            queryParameters.forEach((k, v) -> queries.put(k, new ArrayList<>(v)));
        }
        routerRequestCache.setQueries(queries);
    }

    @Override
    public HttpMethod getMethod() {
        return routerRequestCache.getMethod();
    }

    @Override
    public String getUrl() {
        return routerRequestCache.getUrl();
    }

    @Override
    public List<RouterPathAttribute> getPathAttributes() {
        return routerRequestCache.getPathAttributes();
    }

    @Override
    public Map<String, List<String>> getMatrix() {
        // TODO
        return null;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return routerRequestCache.getHeaders();
    }

    @Override
    public List<HttpCookie> getCookies() {
        return routerRequestCache.getCookies();
    }

    @Override
    public RouterSession getSession() {
        var session = routerRequestCache.getSession();
        if (session == null) {
            try {
                session = new UndertowRouterSession(exchange);
            } catch (Throwable throwable) {
                LOGGER.warn("this request has no session");
            }
            routerRequestCache.setSession(session);
        }
        return session;
    }

    @Override
    public Map<String, List> getParameters() {
        return routerRequestCache.getParameters();
    }

    @Override
    public Map<String, List<String>> getQueries() {
        return routerRequestCache.getQueries();
    }

    @Override
    public InputStream getInputStreamBody() {
        var inputStreamBody = routerRequestCache.getInputStreamBody();
        if (inputStreamBody == null) {
            inputStreamBody = setInputStreamBody();
        }
        return inputStreamBody;
    }

    private InputStream setInputStreamBody() {
        exchange.startBlocking();
        return exchange.getInputStream();
    }

    @Override
    public MediaType getContentType() {
        return routerRequestCache.getContentType();
    }

    @Override
    public MediaType getResponseType() {
        return routerRequestCache.getResponseType();
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getTextBody() {
        return null;
    }

    @Override
    public File getFileBody() {
        return null;
    }

    @Override
    public RouterRequest clone() {
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowRouterRequest.class);
}
