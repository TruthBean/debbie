/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.reflection.ExecutableArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 10:44
 */
public class DefaultRequestParameterParser implements RequestParameterParser {
    @Override
    public RequestParameterInfo parse(Annotation annotation) {
        if (annotation instanceof RequestParameter) {
            RequestParameter requestParameter = (RequestParameter) annotation;
            return new RequestParameterInfo(requestParameter, "");
        }

        if (annotation instanceof BodyParameter) {
            BodyParameter bodyParameter = (BodyParameter) annotation;
            return new RequestParameterInfo(bodyParameter, "");
        }

        if (annotation instanceof CookieParameter) {
            CookieParameter cookieParameter = (CookieParameter) annotation;
            return new RequestParameterInfo(cookieParameter, "");
        }

        if (annotation instanceof HeadParameter) {
            HeadParameter headParameter = (HeadParameter) annotation;
            return new RequestParameterInfo(headParameter, "");
        }

        if (annotation instanceof InnerParameter) {
            InnerParameter innerParameter = (InnerParameter) annotation;
            return new RequestParameterInfo(innerParameter, "");
        }

        if (annotation instanceof MatrixParameter) {
            MatrixParameter matrixParameter = (MatrixParameter) annotation;
            return new RequestParameterInfo(matrixParameter, "");
        }

        if (annotation instanceof ParamParameter) {
            ParamParameter paramParameter = (ParamParameter) annotation;
            return new RequestParameterInfo(paramParameter, "");
        }

        if (annotation instanceof PathParameter) {
            PathParameter pathParameter = (PathParameter) annotation;
            return new RequestParameterInfo(pathParameter, "");
        }

        if (annotation instanceof QueryParameter) {
            QueryParameter queryParameter = (QueryParameter) annotation;
            return new RequestParameterInfo(queryParameter, "");
        }

        if (annotation instanceof SessionParameter) {
            SessionParameter sessionParameter = (SessionParameter) annotation;
            return new RequestParameterInfo(sessionParameter, "");
        }

        return null;
    }

    @Override
    public RequestParameterInfo parse(ExecutableArgument argument) {
        String paramName = argument.getName();
        Annotation annotation = argument.getAnnotation(RequestParameter.class);
        if (annotation != null) {
            RequestParameter requestParameter = (RequestParameter) annotation;
            return new RequestParameterInfo(requestParameter, paramName);
        }

        annotation = argument.getAnnotation(BodyParameter.class);
        if (annotation != null) {
            BodyParameter bodyParameter = (BodyParameter) annotation;
            return new RequestParameterInfo(bodyParameter, paramName);
        }

        annotation = argument.getAnnotation(CookieParameter.class);
        if (annotation != null) {
            CookieParameter cookieParameter = (CookieParameter) annotation;
            return new RequestParameterInfo(cookieParameter, paramName);
        }

        annotation = argument.getAnnotation(HeadParameter.class);
        if (annotation != null) {
            HeadParameter headParameter = (HeadParameter) annotation;
            return new RequestParameterInfo(headParameter, paramName);
        }

        annotation = argument.getAnnotation(InnerParameter.class);
        if (annotation != null) {
            InnerParameter innerParameter = (InnerParameter) annotation;
            return new RequestParameterInfo(innerParameter, paramName);
        }

        annotation = argument.getAnnotation(MatrixParameter.class);
        if (annotation != null) {
            MatrixParameter matrixParameter = (MatrixParameter) annotation;
            return new RequestParameterInfo(matrixParameter, paramName);
        }

        annotation = argument.getAnnotation(ParamParameter.class);
        if (annotation != null) {
            ParamParameter paramParameter = (ParamParameter) annotation;
            return new RequestParameterInfo(paramParameter, paramName);
        }

        annotation = argument.getAnnotation(PathParameter.class);
        if (annotation != null) {
            PathParameter pathParameter = (PathParameter) annotation;
            return new RequestParameterInfo(pathParameter, paramName);
        }

        annotation = argument.getAnnotation(QueryParameter.class);
        if (annotation != null) {
            QueryParameter queryParameter = (QueryParameter) annotation;
            return new RequestParameterInfo(queryParameter, paramName);
        }

        annotation = argument.getAnnotation(SessionParameter.class);
        if (annotation != null) {
            SessionParameter sessionParameter = (SessionParameter) annotation;
            return new RequestParameterInfo(sessionParameter, paramName);
        }

        if (argument.getAnnotations().isEmpty())
            return new RequestParameterInfo(paramName);

        return null;
    }

    @Override
    public RequestParameterInfo parse(Parameter parameter) {
        String paramName = parameter.getName();
        RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
        if (requestParameter != null) {
            return new RequestParameterInfo(requestParameter, paramName);
        }

        BodyParameter bodyParameter = parameter.getAnnotation(BodyParameter.class);
        if (bodyParameter != null) {
            return new RequestParameterInfo(bodyParameter, paramName);
        }

        CookieParameter cookieParameter = parameter.getAnnotation(CookieParameter.class);
        if (cookieParameter != null) {
            return new RequestParameterInfo(cookieParameter, paramName);
        }

        HeadParameter headParameter = parameter.getAnnotation(HeadParameter.class);
        if (headParameter != null) {
            return new RequestParameterInfo(headParameter, paramName);
        }

        InnerParameter innerParameter = parameter.getAnnotation(InnerParameter.class);
        if (innerParameter != null) {
            return new RequestParameterInfo(innerParameter, paramName);
        }

        MatrixParameter matrixParameter = parameter.getAnnotation(MatrixParameter.class);
        if (matrixParameter != null) {
            return new RequestParameterInfo(matrixParameter, paramName);
        }

        ParamParameter paramParameter = parameter.getAnnotation(ParamParameter.class);
        if (paramParameter != null) {
            return new RequestParameterInfo(paramParameter, paramName);
        }

        PathParameter pathParameter = parameter.getAnnotation(PathParameter.class);
        if (pathParameter != null) {
            return new RequestParameterInfo(pathParameter, paramName);
        }

        QueryParameter queryParameter = parameter.getAnnotation(QueryParameter.class);
        if (queryParameter != null) {
            return new RequestParameterInfo(queryParameter, paramName);
        }

        SessionParameter sessionParameter = parameter.getAnnotation(SessionParameter.class);
        if (sessionParameter != null) {
            return new RequestParameterInfo(sessionParameter, paramName);
        }

        return new RequestParameterInfo(paramName);
    }
}
