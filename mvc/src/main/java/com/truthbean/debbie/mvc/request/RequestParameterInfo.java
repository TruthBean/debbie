package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.reflection.ExecutableArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class RequestParameterInfo {

    private String name;

    private String value;

    private RequestParameterType paramType;

    private String defaultValue;

    private boolean require;

    private MediaType bodyType;

    private Annotation annotation;

    public RequestParameterInfo(RequestParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = parameter.paramType();
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = parameter.bodyType();
    }

    public RequestParameterInfo(BodyParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.BODY;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = parameter.bodyType();
    }

    public RequestParameterInfo(CookieParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.COOKIE;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(HeadParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.HEAD;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(InnerParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.INNER;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(MatrixParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.MATRIX;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(ParamParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.PARAM;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(PathParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.PATH;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(QueryParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.QUERY;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(SessionParameter parameter) {
        this.annotation = parameter;
        this.name = parameter.name();
        this.value = parameter.value();
        this.paramType = RequestParameterType.SESSION;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public RequestParameterType paramType() {
        return paramType;
    }

    public String defaultValue() {
        return defaultValue;
    }

    public boolean require() {
        return require;
    }

    public MediaType bodyType() {
        return bodyType;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public static RequestParameterInfo fromExecutableArgumentAnnotation(ExecutableArgument argument) {
        Annotation annotation = argument.getAnnotation(RequestParameter.class);
        if (annotation != null) {
            RequestParameter requestParameter = (RequestParameter) annotation;
            return new RequestParameterInfo(requestParameter);
        }

        annotation = argument.getAnnotation(BodyParameter.class);
        if (annotation != null) {
            BodyParameter bodyParameter = (BodyParameter) annotation;
            return new RequestParameterInfo(bodyParameter);
        }

        annotation = argument.getAnnotation(CookieParameter.class);
        if (annotation != null) {
            CookieParameter cookieParameter = (CookieParameter) annotation;
            return new RequestParameterInfo(cookieParameter);
        }

        annotation = argument.getAnnotation(HeadParameter.class);
        if (annotation != null) {
            HeadParameter headParameter = (HeadParameter) annotation;
            return new RequestParameterInfo(headParameter);
        }

        annotation = argument.getAnnotation(InnerParameter.class);
        if (annotation != null) {
            InnerParameter innerParameter = (InnerParameter) annotation;
            return new RequestParameterInfo(innerParameter);
        }

        annotation = argument.getAnnotation(MatrixParameter.class);
        if (annotation != null) {
            MatrixParameter matrixParameter = (MatrixParameter) annotation;
            return new RequestParameterInfo(matrixParameter);
        }

        annotation = argument.getAnnotation(ParamParameter.class);
        if (annotation != null) {
            ParamParameter paramParameter = (ParamParameter) annotation;
            return new RequestParameterInfo(paramParameter);
        }

        annotation = argument.getAnnotation(PathParameter.class);
        if (annotation != null) {
            PathParameter pathParameter = (PathParameter) annotation;
            return new RequestParameterInfo(pathParameter);
        }

        annotation = argument.getAnnotation(QueryParameter.class);
        if (annotation != null) {
            QueryParameter queryParameter = (QueryParameter) annotation;
            return new RequestParameterInfo(queryParameter);
        }

        annotation = argument.getAnnotation(SessionParameter.class);
        if (annotation != null) {
            SessionParameter sessionParameter = (SessionParameter) annotation;
            return new RequestParameterInfo(sessionParameter);
        }

        return null;
    }

    public static RequestParameterInfo fromAnnotation(Annotation annotation) {
        if (annotation instanceof RequestParameter) {
            RequestParameter requestParameter = (RequestParameter) annotation;
            return new RequestParameterInfo(requestParameter);
        }

        if (annotation instanceof BodyParameter) {
            BodyParameter bodyParameter = (BodyParameter) annotation;
            return new RequestParameterInfo(bodyParameter);
        }

        if (annotation instanceof CookieParameter) {
            CookieParameter cookieParameter = (CookieParameter) annotation;
            return new RequestParameterInfo(cookieParameter);
        }

        if (annotation instanceof HeadParameter) {
            HeadParameter headParameter = (HeadParameter) annotation;
            return new RequestParameterInfo(headParameter);
        }

        if (annotation instanceof InnerParameter) {
            InnerParameter innerParameter = (InnerParameter) annotation;
            return new RequestParameterInfo(innerParameter);
        }

        if (annotation instanceof MatrixParameter) {
            MatrixParameter matrixParameter = (MatrixParameter) annotation;
            return new RequestParameterInfo(matrixParameter);
        }

        if (annotation instanceof ParamParameter) {
            ParamParameter paramParameter = (ParamParameter) annotation;
            return new RequestParameterInfo(paramParameter);
        }

        if (annotation instanceof PathParameter) {
            PathParameter pathParameter = (PathParameter) annotation;
            return new RequestParameterInfo(pathParameter);
        }

        if (annotation instanceof QueryParameter) {
            QueryParameter queryParameter = (QueryParameter) annotation;
            return new RequestParameterInfo(queryParameter);
        }

        if (annotation instanceof SessionParameter) {
            SessionParameter sessionParameter = (SessionParameter) annotation;
            return new RequestParameterInfo(sessionParameter);
        }

        return null;
    }

    public static RequestParameterInfo fromParameterAnnotation(Parameter parameter) {
        RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
        if (requestParameter != null) {
            return new RequestParameterInfo(requestParameter);
        }

        BodyParameter bodyParameter = parameter.getAnnotation(BodyParameter.class);
        if (bodyParameter != null) {
            return new RequestParameterInfo(bodyParameter);
        }

        CookieParameter cookieParameter = parameter.getAnnotation(CookieParameter.class);
        if (cookieParameter != null) {
            return new RequestParameterInfo(cookieParameter);
        }

        HeadParameter headParameter = parameter.getAnnotation(HeadParameter.class);
        if (headParameter != null) {
            return new RequestParameterInfo(headParameter);
        }

        InnerParameter innerParameter = parameter.getAnnotation(InnerParameter.class);
        if (innerParameter != null) {
            return new RequestParameterInfo(innerParameter);
        }

        MatrixParameter matrixParameter = parameter.getAnnotation(MatrixParameter.class);
        if (matrixParameter != null) {
            return new RequestParameterInfo(matrixParameter);
        }

        ParamParameter paramParameter = parameter.getAnnotation(ParamParameter.class);
        if (paramParameter != null) {
            return new RequestParameterInfo(paramParameter);
        }

        PathParameter pathParameter = parameter.getAnnotation(PathParameter.class);
        if (pathParameter != null) {
            return new RequestParameterInfo(pathParameter);
        }

        QueryParameter queryParameter = parameter.getAnnotation(QueryParameter.class);
        if (queryParameter != null) {
            return new RequestParameterInfo(queryParameter);
        }

        SessionParameter sessionParameter = parameter.getAnnotation(SessionParameter.class);
        if (sessionParameter != null) {
            return new RequestParameterInfo(sessionParameter);
        }

        return null;
    }
}
