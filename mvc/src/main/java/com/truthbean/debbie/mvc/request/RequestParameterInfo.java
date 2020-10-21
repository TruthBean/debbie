/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.debbie.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RequestParameterInfo {

    private final String name;

    private final String value;

    private final RequestParameterType paramType;

    private final String defaultValue;

    private final boolean require;

    private final MediaType bodyType;

    private final Annotation annotation;

    public RequestParameterInfo(RequestParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = parameter.paramType();
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = parameter.bodyType();
    }

    public RequestParameterInfo(BodyParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.BODY;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = parameter.type();
    }

    public RequestParameterInfo(CookieParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.COOKIE;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(HeadParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.HEAD;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(InnerParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.INNER;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(MatrixParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.MATRIX;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(ParamParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.PARAM;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(PathParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.PATH;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(QueryParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.QUERY;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(SessionParameter parameter, String paramName) {
        this.annotation = parameter;
        String name = parameter.name();
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = parameter.value();
        this.paramType = RequestParameterType.SESSION;
        this.defaultValue = parameter.defaultValue();
        this.require = parameter.require();
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(String paramName) {
        this.annotation = null;
        String name = paramName;
        if (StringUtils.isBlank(name)) {
            name = paramName;
        }
        this.name = name;
        this.value = "";
        this.paramType = RequestParameterType.MIX;
        this.defaultValue = "";
        this.require = false;
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo() {
        this.annotation = null;
        this.name = "";
        this.value = "";
        this.paramType = RequestParameterType.MIX;
        this.defaultValue = "";
        this.require = false;
        this.bodyType = MediaType.ANY;
    }

    public RequestParameterInfo(String name, String value, RequestParameterType paramType, String defaultValue,
                                boolean require, MediaType bodyType, Annotation annotation) {
        this.name = name;
        this.value = value;
        this.paramType = paramType;
        this.defaultValue = defaultValue;
        this.require = require;
        this.bodyType = bodyType;
        this.annotation = annotation;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;

        private String value;

        private RequestParameterType paramType;

        private String defaultValue;

        private boolean require;

        private MediaType bodyType;

        private Annotation annotation;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder paramType(RequestParameterType paramType) {
            this.paramType = paramType;
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder require(boolean require) {
            this.require = require;
            return this;
        }

        public Builder bodyType(MediaType bodyType) {
            this.bodyType = bodyType;
            return this;
        }

        public Builder annotation(Annotation annotation) {
            this.annotation = annotation;
            return this;
        }

        public RequestParameterInfo build() {
            return new RequestParameterInfo(name, value, paramType, defaultValue, require, bodyType, annotation);
        }
    }

    public static RequestParameterInfo fromExecutableArgumentAnnotation(ExecutableArgument argument) {
        DefaultRequestParameterParser parameterParser = new DefaultRequestParameterParser();
        RequestParameterInfo parameterInfo = parameterParser.parse(argument);
        if (parameterInfo == null) {
            Set<RequestParameterParser> parsers = SpiLoader.loadProviderSet(RequestParameterParser.class);
            for (RequestParameterParser parser : parsers) {
                RequestParameterInfo info = parser.parse(argument);
                if (info != null) {
                    return info;
                }
            }
            return null;
        }
        return parameterInfo;
    }

    public static RequestParameterInfo fromAnnotation(Annotation annotation) {
        DefaultRequestParameterParser parameterParser = new DefaultRequestParameterParser();
        RequestParameterInfo parameterInfo = parameterParser.parse(annotation);
        if (parameterInfo == null) {
            Set<RequestParameterParser> parsers = SpiLoader.loadProviderSet(RequestParameterParser.class);
            for (RequestParameterParser parser : parsers) {
                RequestParameterInfo info = parser.parse(annotation);
                if (info != null) {
                    return info;
                }
            }
            return null;
        }
        return parameterInfo;
    }

    public static RequestParameterInfo fromParameterAnnotation(Parameter parameter) {
        DefaultRequestParameterParser parameterParser = new DefaultRequestParameterParser();
        RequestParameterInfo parameterInfo = parameterParser.parse(parameter);
        if (parameterInfo == null) {
            Set<RequestParameterParser> parsers = SpiLoader.loadProviders(RequestParameterParser.class);
            for (RequestParameterParser parser : parsers) {
                RequestParameterInfo info = parser.parse(parameter);
                if (info != null) {
                    return info;
                }
            }
            return null;
        }
        return parameterInfo;
    }
}
