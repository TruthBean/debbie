package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.core.io.MediaType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterResponse {
    private boolean redirect;

    private boolean hasTemplate;
    private String templateSuffix;
    private String templatePrefix;

    private final Map<String, String> headers = new HashMap<>();
    private MediaType responseType;
    private Object content;

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public java.util.Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public MediaType getResponseType() {
        return responseType;
    }

    public void setResponseType(MediaType responseType) {
        this.responseType = responseType;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public boolean isHasTemplate() {
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

    public void copyFrom(RouterResponse response) {
        this.redirect = response.redirect;
        this.headers.putAll(response.headers);
        this.content = response.content;

        this.hasTemplate = response.hasTemplate;
        this.templatePrefix = response.templatePrefix;
        this.templateSuffix = response.templateSuffix;
    }
}
