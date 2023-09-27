/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.lang.Copyable;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;
import com.truthbean.core.util.StringUtils;

import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterResponse extends BaseRouterResponse implements Copyable<RouterResponse> {
    private boolean redirect;

    private boolean hasTemplate;
    private String templateSuffix;
    private String templatePrefix;

    private final Set<HttpCookie> cookies = new HashSet<>();

    private final Map<String, Object> modelAttributes = new HashMap<>();

    private MediaTypeInfo responseType;

    private Class<?> restResponseClass;
    private AbstractResponseContentHandler<?, ?> handler;

    private Charset charset;

    private boolean error;

    public RouterResponse() {
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public void addCookie(HttpCookie cookie) {
        String name = cookie.getName();
        List<HttpCookie> copy = new ArrayList<>(cookies);
        for (HttpCookie httpCookie : copy) {
            if (httpCookie != null && httpCookie.getName().equals(name)) {
                cookies.remove(httpCookie);
            }
        }
        cookies.add(cookie);
    }

    public Set<HttpCookie> getCookies() {
        return Set.copyOf(cookies);
    }

    public void addModelAttribute(String key, Object value) {
        modelAttributes.put(key, value);
    }

    public Map<String, Object> getModelAttributes() {
        return Map.copyOf(modelAttributes);
    }

    public MediaTypeInfo getResponseType() {
        if (responseType == null) {
            var contentType = "Content-Type";
            Map<String, String> headers = getHeaders();
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

    public AbstractResponseContentHandler<?, ?> getHandler() {
        return handler;
    }

    public void setCharacterEncoding(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
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

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void copyFrom(RouterResponse response) {
        super.copyFrom(response);
        this.redirect = response.redirect;
        this.handler = response.handler;

        this.cookies.addAll(response.cookies);
        this.modelAttributes.putAll(response.modelAttributes);

        this.hasTemplate = response.hasTemplate;
        this.templatePrefix = response.templatePrefix;
        this.templateSuffix = response.templateSuffix;

        this.restResponseClass = response.restResponseClass;

        this.error = response.error;
    }

    public void copyNoNull(RouterResponse response) {
        super.copyNoNull(response);
        this.redirect = response.redirect;

        if (response.handler != null)
            this.handler = response.handler;

        this.cookies.addAll(response.cookies);
        this.modelAttributes.putAll(response.modelAttributes);

        if (response.hasTemplate && !this.hasTemplate) {
            this.hasTemplate = true;
        }

        if (StringUtils.hasText(response.templatePrefix))
            this.templatePrefix = response.templatePrefix;
        if (StringUtils.hasText(response.templateSuffix))
            this.templateSuffix = response.templateSuffix;

        if (response.restResponseClass != null)
            this.restResponseClass = response.restResponseClass;
    }

    @Override
    public RouterResponse copy() {
        RouterResponse response;
        try {
            response = (RouterResponse) super.clone();
        } catch (CloneNotSupportedException e) {
            response = new RouterResponse();
        }

        response.copyFrom(this);

        response.redirect = this.redirect;

        response.responseType = responseType;

        response.restResponseClass = this.restResponseClass;
        response.handler = this.handler;

        response.cookies.addAll(this.cookies);
        response.modelAttributes.putAll(this.modelAttributes);

        response.hasTemplate = this.hasTemplate;
        response.templatePrefix = this.templatePrefix;
        response.templateSuffix = this.templateSuffix;

        response.error = this.error;

        return response;
    }

    @Override
    public String toString() {
        return "{" + "\"redirect\":" + redirect + "," + "\"hasTemplate\":" + hasTemplate + ","
                + "\"templateSuffix\":\"" + templateSuffix + "\"" + ","
                + "\"templatePrefix\":\"" + templatePrefix + "\"" + "," + "\"headers\":" + getHeaders() + ","
                + "\"cookies\":" + cookies + "," + "\"responseType\":" + responseType + ","
                + "\"content\":" + getContent() + ","
                + "\"status\":" + getStatus() + "," + "\"handler\":" + handler + "}";
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
        response.headers.putAll(this.getHeaders());

        response.responseType = responseType.toString();

        response.restResponseClass = this.restResponseClass.getName();
        response.content = this.getContent();

        response.cookies.addAll(this.cookies);

        response.hasTemplate = this.hasTemplate;
        response.templatePrefix = this.templatePrefix;
        response.templateSuffix = this.templateSuffix;
        response.modelAttributes.putAll(this.modelAttributes);
        return response;
    }
}
