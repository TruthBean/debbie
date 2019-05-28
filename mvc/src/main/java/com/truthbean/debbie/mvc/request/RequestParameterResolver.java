package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.core.data.validate.DataValidateFactory;
import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.reflection.ExecutableArgument;
import com.truthbean.debbie.core.reflection.ExecutableArgumentResolver;
import com.truthbean.debbie.mvc.router.RouterMethodArgumentHandler;
import com.truthbean.debbie.mvc.router.RouterRequestValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        var annotation = parameter.getAnnotation(RequestParameter.class);
        if (annotation != null) {
            this.handler = new RouterMethodArgumentHandler();
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
        var annotation = parameter.getAnnotation(RequestParameter.class);
        RequestParameter requestParameter = (RequestParameter) annotation;
        LOGGER.debug("annotation is RequestParameter");

        RouterRequestValues parameters = (RouterRequestValues) originValues;

        boolean result = false;

        switch (requestParameter.paramType()) {
            case MIX:
                Map<String, List> mixValues = parameters.getMixValues();
                handler.handleParam(mixValues, parameter);
                break;
            case QUERY:
                handler.handleParam(parameters.getQueries(), parameter);
                break;
            case PATH:
                handler.handleParam(parameters.getPathAttributes(), parameter);
                break;
            case MATRIX:
                Map<String, List> matrix = parameters.getMatrixAttributes();
                handler.handleParam(matrix, parameter);
                break;
            case PARAM:
                Map<String, List> params = parameters.getParams();
                handler.handleParam(params, parameter);
                break;
            case BODY:
                var type = requestParameter.bodyType();
                if (type == MediaType.ANY) {
                    type = requestType;
                }
                String textBody = parameters.getTextBody();
                if (textBody == null) {
                    handler.handleStream(parameters.getBody(), type, parameter);
                } else {
                    handler.handleStream(textBody, type, parameter);
                }
                break;
            case HEAD:
                Map<String, List> headers = parameters.getHeaders();
                handler.handleParam(headers, parameter);
                break;
            case COOKIE:
                Map<String, List> cookieAttributes = parameters.getCookieAttributes();
                handler.handleParam(cookieAttributes, parameter);
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
            throw new IllegalArgumentException(requestParameter.name() + " has no value! ");
        }

        if (!requestParameter.require() && parameter.getValue() == null) {
            handler.handleParam(parameter.getName(), requestParameter.defaultValue(), parameter);
        }
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestParameterResolver.class);
}
