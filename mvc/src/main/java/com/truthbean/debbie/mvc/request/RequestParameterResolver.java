/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.data.validate.DataValidateFactory;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.router.RouterMethodArgumentHandler;
import com.truthbean.debbie.mvc.router.RouterRequestValues;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentResolver;
import com.truthbean.debbie.reflection.TypeHelper;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class RequestParameterResolver implements ExecutableArgumentResolver {

    private RouterMethodArgumentHandler handler;

    @Override
    public boolean supportsParameter(ExecutableArgument parameter) {
        RequestParameterInfo requestParameterInfo = RequestParameterInfo.fromExecutableArgumentAnnotation(parameter);
        if (requestParameterInfo != null) {
            this.handler = new RouterMethodArgumentHandler(parameter.getClassLoader());
            return true;
        }
        return false;
    }

    private MediaType requestType;

    public void setRequestType(MediaType requestType) {
        this.requestType = requestType;
    }

    @Override
    public boolean resolveArgument(ExecutableArgument parameter, Object originValues, DataValidateFactory validateFactory) {
        RequestParameterInfo requestParameter = RequestParameterInfo.fromExecutableArgumentAnnotation(parameter);
        if (requestParameter == null) {
            throw new NullPointerException("RequestParameter annotation miss");
        }
        LOGGER.debug("annotation is RequestParameter");

        RouterRequestValues parameters = (RouterRequestValues) originValues;

        boolean result = false;

        switch (requestParameter.paramType()) {
            case MIX:
                Map<String, List<Object>> mixValues = parameters.getMixValues();
                handler.handleObjectParam(mixValues, parameter, false);
                if (parameter.getValue() == null) {
                    if (TypeHelper.isBaseType(parameter.getType())) {
                        break;
                    }
                    handler.handleFields(parameters, parameter, requestType);
                }
                break;
            case QUERY:
                handler.handleParam(parameters.getQueries(), parameter, false);
                if (parameter.getValue() == null) {
                    if (TypeHelper.isBaseType(parameter.getType())) {
                        break;
                    }
                    handler.handleFields(parameters, parameter, requestType);
                }
                break;
            case PATH:
                handler.handleParam(parameters.getPathAttributes(), parameter, false);
                if (parameter.getValue() == null) {
                    if (TypeHelper.isBaseType(parameter.getType())) {
                        break;
                    }
                    handler.handleFields(parameters, parameter, requestType);
                }
                break;
            case MATRIX:
                Map<String, List<String>> matrix = parameters.getMatrixAttributes();
                handler.handleParam(matrix, parameter, false);
                if (parameter.getValue() == null) {
                    if (TypeHelper.isBaseType(parameter.getType())) {
                        break;
                    }
                    handler.handleFields(parameters, parameter, requestType);
                }
                break;
            case PARAM:
                Map<String, List<Object>> params = parameters.getParams();
                handler.handleObjectParam(params, parameter, false);
                if (parameter.getValue() == null) {
                    if (TypeHelper.isBaseType(parameter.getType())) {
                        break;
                    }
                    handler.handleFields(parameters, parameter, requestType);
                }
                break;
            case BODY:
                var type = requestParameter.bodyType();
                if (type == MediaType.ANY) {
                    type = requestType;
                }
                if (requestParameter.bodyType().isText()) {
                    String textBody = parameters.getTextBody();
                    if (textBody == null) {
                        handler.handleStream(parameters.getBody(), type, parameter);
                    } else {
                        handler.handleStream(textBody, type, parameter);
                    }
                } else {
                    handler.handleStream(parameters.getBody(), type, parameter);
                }
                break;
            case HEAD:
                Map<String, List<String>> headers = parameters.getHeaders();
                handler.handleParam(headers, parameter, true);
                if (parameter.getValue() == null) {
                    if (TypeHelper.isBaseType(parameter.getType())) {
                        break;
                    }
                    handler.handleFields(parameters, parameter, requestType);
                }
                break;
            case COOKIE:
                Map<String, List<Object>> cookieAttributes = parameters.getCookieAttributes();
                handler.handleObjectParam(cookieAttributes, parameter, true);
                if (parameter.getValue() == null) {
                    if (TypeHelper.isBaseType(parameter.getType())) {
                        break;
                    }
                    handler.handleFields(parameters, parameter, requestType);
                }
                break;
            case SESSION:
                Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
                handler.handleObject(sessionAttributes, parameter);
                result = true;
                break;
            case INNER:
                Map<String, Object> requestAttributes = parameters.getInnerAttributes();
                handler.handleObject(requestAttributes, parameter);
                result = true;
                break;
            default:
                break;
        }

        if (requestParameter.require() && parameter.getValue() == null) {
            throw new IllegalArgumentException(parameter.stack() + " has no VALUE! ");
        }

        if (!requestParameter.require() && parameter.getValue() == null) {
            handler.handleParam(parameter.getName(), requestParameter.defaultValue(), parameter);
        }
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestParameterResolver.class);
}
