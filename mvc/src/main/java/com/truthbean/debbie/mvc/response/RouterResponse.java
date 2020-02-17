package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;

import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterResponse implements Cloneable {
    private boolean redirect;

    private boolean hasTemplate;
    private String templateSuffix;
    private String templatePrefix;

    private final Map<String, String> headers = new HashMap<>();
    private final List<HttpCookie> cookies = new ArrayList<>();

    private final Map<String, Object> modelAttributes = new HashMap<>();

    private MediaTypeInfo responseType;

    private Class<?> restResponseClass;
    private Object content;
    private AbstractResponseContentHandler<?, ?> handler;

    private Charset charset;
    private HttpStatus status;

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addHeader(HttpHeader.HttpHeaderName headerName, String value) {
        headers.put(headerName.getName(), value);
    }

    public void addCookie(HttpCookie cookie) {
        cookies.add(cookie);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public List<HttpCookie> getCookies() {
        return Collections.unmodifiableList(cookies);
    }

    public void addModelAttribute(String key, Object value) {
        modelAttributes.put(key, value);
    }

    public Map<String, Object> getModelAttributes() {
        return Collections.unmodifiableMap(modelAttributes);
    }

    public MediaTypeInfo getResponseType() {
        if (responseType == null) {
            var contentType = "Content-Type";
            if (headers.containsKey(contentType)) {
                responseType = MediaTypeInfo.parse(headers.get(contentType));
            } else {
                contentType = "content-type";
                if (headers.containsKey(contentType)) {
                    responseType = MediaTypeInfo.parse(contentType);
                } else {
                    contentType = "CONTENT-TYPE";
                    if (headers.containsKey(contentType)) {
                        responseType = MediaTypeInfo.parse(contentType);
                    }
                }
            }
        }
        return responseType;
    }

    public void setResponseType(MediaType responseType) {
        this.responseType = responseType.info();
    }

    public void setResponseType(MediaTypeInfo mediaTypeInfo) {
        this.responseType = mediaTypeInfo;
    }

    public boolean hasTemplate() {
        return hasTemplate;
    }

    public void setHasTemplate(boolean hasTemplate) {
        this.hasTemplate = hasTemplate;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public void setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
    }

    public String getTemplatePrefix() {
        return templatePrefix;
    }

    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public AbstractResponseContentHandler<?, ?> getHandler() {
        return handler;
    }

    public void setCharacterEncoding(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHandler(AbstractResponseContentHandler<?, ?> handler) {
        if (handler != null && handler.getClass() != NothingResponseHandler.class) {
            this.handler = handler;
            if (this.responseType == null || this.responseType.isSameMediaType(MediaType.ANY))
                this.responseType = handler.getResponseType();
        }
    }

    public Class<?> getRestResponseClass() {
        return restResponseClass;
    }

    public void setRestResponseClass(Class<?> restResponseClass) {
        this.restResponseClass = restResponseClass;
    }

    public void copyFrom(RouterResponse response) {
        this.redirect = response.redirect;
        this.headers.putAll(response.headers);

        this.content = response.content;
        this.handler = response.handler;

        this.cookies.addAll(response.cookies);
        this.modelAttributes.putAll(response.modelAttributes);

        this.hasTemplate = response.hasTemplate;
        this.templatePrefix = response.templatePrefix;
        this.templateSuffix = response.templateSuffix;

        this.restResponseClass = response.restResponseClass;

        this.status = response.status;
    }

    @Override
    public RouterResponse clone() {
        RouterResponse response = new RouterResponse();
        response.redirect = this.redirect;
        response.headers.putAll(this.headers);

        response.responseType = responseType;

        response.restResponseClass = this.restResponseClass;
        response.content = this.content;
        response.handler = this.handler;

        response.cookies.addAll(this.cookies);
        response.modelAttributes.putAll(this.modelAttributes);

        response.hasTemplate = this.hasTemplate;
        response.templatePrefix = this.templatePrefix;
        response.templateSuffix = this.templateSuffix;

        response.status = this.status;

        return response;
    }

    @Override
    public String toString() {
        return "{" + "\"redirect\":" + redirect + "," + "\"hasTemplate\":" + hasTemplate + ","
                + "\"templateSuffix\":\"" + templateSuffix + "\"" + ","
                + "\"templatePrefix\":\"" + templatePrefix + "\"" + "," + "\"headers\":" + headers + ","
                + "\"cookies\":" + cookies + "," + "\"responseType\":" + responseType + ","
                + "\"content\":" + content + "," + "\"handler\":" + handler + "}";
    }

    public static class RouterJsonResponse {
        private boolean redirect;

        private boolean hasTemplate;
        private String templateSuffix;
        private String templatePrefix;

        private final Map<String, String> headers = new HashMap<>();
        private final List<HttpCookie> cookies = new ArrayList<>();
        private final Map<String, Object> modelAttributes = new HashMap<>();

        private String responseType;

        private String restResponseClass;
        private Object content;

        public boolean isRedirect() {
            return redirect;
        }

        public boolean isHasTemplate() {
            return hasTemplate;
        }

        public String getTemplateSuffix() {
            return templateSuffix;
        }

        public String getTemplatePrefix() {
            return templatePrefix;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public List<HttpCookie> getCookies() {
            return cookies;
        }

        public Map<String, Object> getModelAttributes() {
            return modelAttributes;
        }

        public String getResponseType() {
            return responseType;
        }

        public String getRestResponseClass() {
            return restResponseClass;
        }

        public Object getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"redirect\":" + redirect +
                    ",\"hasTemplate\":" + hasTemplate +
                    ",\"templateSuffix\":\"" + templateSuffix + '\"' +
                    ",\"templatePrefix\":\"" + templatePrefix + '\"' +
                    ",\"headers\":" + headers +
                    ",\"cookies\":" + cookies +
                    ",\"responseType\":\"" + responseType + '\"' +
                    ",\"restResponseClass\":\"" + restResponseClass + '\"' +
                    ",\"content\":" + content +
                    '}';
        }
    }

    public RouterJsonResponse toJsonInfo() {
        RouterJsonResponse response = new RouterJsonResponse();
        response.redirect = this.redirect;
        response.headers.putAll(this.headers);

        response.responseType = responseType.toString();

        response.restResponseClass = this.restResponseClass.getName();
        response.content = this.content;

        response.cookies.addAll(this.cookies);

        response.hasTemplate = this.hasTemplate;
        response.templatePrefix = this.templatePrefix;
        response.templateSuffix = this.templateSuffix;
        response.modelAttributes.putAll(this.modelAttributes);
        return response;
    }
}
