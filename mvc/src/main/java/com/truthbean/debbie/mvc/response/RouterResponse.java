package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

import java.net.HttpCookie;
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

    private MediaTypeInfo responseType;

    private Object content;
    private AbstractResponseContentHandler<?, ?> handler;

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
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

    public void setHandler(AbstractResponseContentHandler<?, ?> handler) {
        this.handler = handler;
    }

    public void copyFrom(RouterResponse response) {
        this.redirect = response.redirect;
        this.headers.putAll(response.headers);

        this.content = response.content;
        this.handler = response.handler;

        this.cookies.addAll(response.cookies);

        this.hasTemplate = response.hasTemplate;
        this.templatePrefix = response.templatePrefix;
        this.templateSuffix = response.templateSuffix;
    }

    @Override
    public RouterResponse clone() {
        RouterResponse response = new RouterResponse();
        response.redirect = this.redirect;
        response.headers.putAll(this.headers);

        response.responseType = responseType;

        response.content = this.content;
        response.handler = this.handler;

        response.cookies.addAll(this.cookies);

        response.hasTemplate = this.hasTemplate;
        response.templatePrefix = this.templatePrefix;
        response.templateSuffix = this.templateSuffix;
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
}
