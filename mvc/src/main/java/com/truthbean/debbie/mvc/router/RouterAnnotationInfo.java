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

import java.lang.reflect.Method;
import java.util.Arrays;

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

    public BeanType type() {
        return type;
    }

    public String[] value() {
        return value;
    }

    public String[] urlPatterns() {
        return urlPatterns;
    }

    public HttpMethod[] method() {
        return method;
    }

    public MediaType requestType() {
        return requestType;
    }

    public boolean hasTemplate() {
        return hasTemplate;
    }

    public String templateSuffix() {
        return templateSuffix;
    }

    public String templatePrefix() {
        return templatePrefix;
    }

    public MediaType responseType() {
        return responseType;
    }

    public Class<? extends AbstractResponseContentHandler> handlerClass() {
        return handlerClass;
    }

    public static RouterAnnotationInfo getRouterAnnotation(Method method) {
        Router router = method.getAnnotation(Router.class);
        if (router != null) {
            return new RouterAnnotationInfo(router);
        }

        GetRouter getRouter = method.getAnnotation(GetRouter.class);
        if (getRouter != null) {
            return new RouterAnnotationInfo(getRouter);
        }

        PostRouter postRouter = method.getAnnotation(PostRouter.class);
        if (postRouter != null) {
            return new RouterAnnotationInfo(postRouter);
        }

        PutRouter putRouter = method.getAnnotation(PutRouter.class);
        if (putRouter != null) {
            return new RouterAnnotationInfo(putRouter);
        }

        DeleteRouter deleteRouter = method.getAnnotation(DeleteRouter.class);
        if (deleteRouter != null) {
            return new RouterAnnotationInfo(deleteRouter);
        }

        OptionsRouter optionsRouter = method.getAnnotation(OptionsRouter.class);
        if (optionsRouter != null) {
            return new RouterAnnotationInfo(optionsRouter);
        }

        HeadRouter headRouter = method.getAnnotation(HeadRouter.class);
        if (headRouter != null) {
            return new RouterAnnotationInfo(headRouter);
        }

        PatchRouter patchRouter = method.getAnnotation(PatchRouter.class);
        if (patchRouter != null) {
            return new RouterAnnotationInfo(patchRouter);
        }

        TraceRouter traceRouter = method.getAnnotation(TraceRouter.class);
        if (traceRouter != null) {
            return new RouterAnnotationInfo(traceRouter);
        }

        ConnectRouter connectRouter = method.getAnnotation(ConnectRouter.class);
        if (connectRouter != null) {
            return new RouterAnnotationInfo(connectRouter);
        }

        // todo custom annotation

        return null;
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
