/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class RouterAnnotationInfo {

    private String name;

    private final BeanType type = BeanType.SINGLETON;

    private String[] value;

    private String[] urlPatterns;

    private HttpMethod[] method;

    private MediaType requestType;

    private boolean hasTemplate;

    private String templateSuffix;

    private String templatePrefix;

    private MediaType responseType;

    @SuppressWarnings("rawtypes")
    private Class<? extends AbstractResponseContentHandler> handlerClass;

    public RouterAnnotationInfo() {
    }

    public RouterAnnotationInfo(Router router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = router.method();
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(ConnectRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.CONNECT};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(GetRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.GET};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(HeadRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.HEAD};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(OptionsRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.OPTIONS};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(PatchRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.PATCH};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(PostRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.POST};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(PutRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.PUT};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(DeleteRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.DELETE};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public RouterAnnotationInfo(TraceRouter router) {
        this.name = router.name();
        this.value = router.value();
        this.urlPatterns = router.urlPatterns();
        this.method = new HttpMethod[]{HttpMethod.DELETE};
        this.requestType = router.requestType();
        this.hasTemplate = router.hasTemplate();
        this.templateSuffix = router.templateSuffix();
        this.templatePrefix = router.templatePrefix();
        this.responseType = router.responseType();
        this.handlerClass = router.handlerClass();
    }

    public String name() {
        return name;
    }

    public RouterAnnotationInfo setName(String name) {
        this.name = name;
        return this;
    }

    public BeanType type() {
        return type;
    }

    public String[] value() {
        return value;
    }

    public RouterAnnotationInfo setValue(String[] value) {
        this.value = value;
        return this;
    }

    public String[] urlPatterns() {
        return urlPatterns;
    }

    public RouterAnnotationInfo setUrlPatterns(String[] urlPatterns) {
        this.urlPatterns = urlPatterns;
        return this;
    }

    public HttpMethod[] method() {
        return method;
    }

    public RouterAnnotationInfo setMethod(HttpMethod[] method) {
        this.method = method;
        return this;
    }

    public RouterAnnotationInfo setMethod(Collection<HttpMethod> methods) {
        this.method = methods.toArray(value -> new HttpMethod[0]);
        return this;
    }

    public MediaType requestType() {
        return requestType;
    }

    public RouterAnnotationInfo setRequestType(MediaType requestType) {
        this.requestType = requestType;
        return this;
    }

    public RouterAnnotationInfo setRequestType(String mediaType) {
        this.requestType = MediaType.of(mediaType);
        return this;
    }

    public boolean hasTemplate() {
        return hasTemplate;
    }

    public RouterAnnotationInfo setHasTemplate(boolean hasTemplate) {
        this.hasTemplate = hasTemplate;
        return this;
    }

    public String templateSuffix() {
        return templateSuffix;
    }

    public RouterAnnotationInfo setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
        return this;
    }

    public String templatePrefix() {
        return templatePrefix;
    }

    public RouterAnnotationInfo setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
        return this;
    }

    public MediaType responseType() {
        return responseType;
    }

    public RouterAnnotationInfo setResponseType(MediaType responseType) {
        this.responseType = responseType;
        return this;
    }

    @SuppressWarnings("rawtypes")
    public Class<? extends AbstractResponseContentHandler> handlerClass() {
        return handlerClass;
    }

    @SuppressWarnings("rawtypes")
    public RouterAnnotationInfo setHandlerClass(Class<? extends AbstractResponseContentHandler> handlerClass) {
        this.handlerClass = handlerClass;
        return this;
    }

    @Override
    public String toString() {
        return "{"
            + "\"name\":\"" + name + '\"' + ","
            + "\"type\":" + type + ","
            + "\"value\":" + Arrays.toString(value) + ","
            + "\"urlPatterns\":" + Arrays.toString(urlPatterns) + ","
            + "\"method\":" + Arrays.toString(method) + ","
            + "\"requestType\":" + requestType + ","
            + "\"hasTemplate\":" + hasTemplate + ","
            + "\"templateSuffix\":\"" + templateSuffix + '\"' + ","
            + "\"templatePrefix\":\"" + templatePrefix + '\"' + ","
            + "\"responseType\":" + responseType + ","
            + "\"handlerClass\":" + handlerClass
            + '}';
    }
}
