package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.data.validate.DefaultDataValidateFactory;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterResolver;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.mvc.response.view.AbstractView;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import com.truthbean.debbie.reflection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author TruthBean
 * Created on 2018-04-08 10:05.
 * @since 0.0.1
 */
public class RouterMethodArgumentHandler extends ExecutableArgumentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterMethodArgumentHandler.class);

    public List handleMethodParams(RouterRequestValues parameters, List<ExecutableArgument> methodParams, MediaType requestType) {
        List<Object> result = new LinkedList<>();
        for (ExecutableArgument invokedParameter : methodParams) {
            LOGGER.debug("invokedParameter " + invokedParameter.getType().getName());
            if (invokedParameter.getType() == RouterSession.class) {
                invokedParameter.setValue(parameters.getRouterSession());
                continue;
            }
            if (invokedParameter.getType() == RouterRequest.class) {
                invokedParameter.setValue(parameters.getRouterRequest());
                continue;
            }
            if (invokedParameter.getType() == RouterResponse.class) {
                RouterRequest routerRequest = parameters.getRouterRequest();
                RouterResponse routerResponse = parameters.getRouterResponse();
                if (routerResponse.getResponseType() == null) {
                    routerResponse.setResponseType(routerRequest.getResponseType());
                }
                invokedParameter.setValue(routerResponse);
                continue;
            }
            handle(parameters, invokedParameter, requestType);
        }

        for (ExecutableArgument invokedParameter : methodParams) {
            result.add(invokedParameter.getValue());
        }

        return result;
    }

    private static boolean isTemplateView(Class<?> clazz) {
        return clazz == AbstractTemplateView.class;
    }

    public void handleInstance(RouterRequestValues parameters, Object newInstance, ExecutableArgument invokedParameter) {
        if (invokedParameter.getType() == AbstractView.class) {
            invokedParameter.setValue(new StaticResourcesView());
            return;
        }

        if (TypeHelper.isBaseType(invokedParameter.getType())
                || invokedParameter.getType() == MultipartFile.class) {

            /*Map<String, List> mixValues = parameters.getMixValues();

            Annotation annotation = invokedParameter.getAnnotation(RequestParameter.class);
            if (annotation != null) {
                RequestParameter requestParameter = (RequestParameter) annotation;

                switch (requestParameter.paramType()) {

                }
            }*/

            // query
            handleParam(parameters.getQueries(), invokedParameter);
            // param
            handleParam(parameters.getParams(), invokedParameter);
            // header
            handleParam(parameters.getHeaders(), invokedParameter);
            // path
            handleParam(parameters.getPathAttributes(), invokedParameter);
            // cookie
            handleParam(parameters.getCookieAttributes(), invokedParameter);
            // session
            handleObject(parameters.getSessionAttributes(), invokedParameter);
            // inner
            handleObject(parameters.getInnerAttributes(), invokedParameter);
            // body
            InputStream body = parameters.getBody();
            if (body != null) {
                // TODO
                // handleBody(body, invokedParameter);
            }
        } else {
            List<Field> fields = ReflectionHelper.getDeclaredFields(newInstance.getClass());
            int i = 0;
            while (i < fields.size()) {
                ExecutableArgument parameter = typeOf(fields.get(i), i);
                if (!TypeHelper.isBaseType(parameter.getType()) && parameter.getType() != MultipartFile.class) {
                    handleInstance(parameters, newInstance, invokedParameter);
                } else {
                    Map<String, List> queries = parameters.getQueries();
                    if (queries != null && !queries.isEmpty()) {
                        handleFiled(queries, newInstance, fields.get(i), parameter);
                    }
                    Map<String, List> params = parameters.getParams();
                    if (params != null && !params.isEmpty()) {
                        handleFiled(params, newInstance, fields.get(i), parameter);
                    }
                    if (parameters.getBody() != null) {
                                /*handleObjectFiled(parameters.getBody(), requestParam.requestType(), newInstance,
                                        fields.get(i), parameter);*/
                    }

                    Map<String, List> headers = parameters.getHeaders();
                    if (headers != null && !headers.isEmpty()) {
                        handleFiled(headers, newInstance, fields.get(i), parameter);
                    }
                    Map<String, List> cookieAttributes = parameters.getCookieAttributes();
                    if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
                        handleFiled(cookieAttributes, newInstance, fields.get(i), parameter);
                    }
                    Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
                    if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
                        // handleFiled(sessionAttributes,newInstance, fields.get(i), parameter);
                    }
                }
            }
            i++;
        }
    }

    /**
     *
     * @param parameters router request values
     * @param invokedParameter method parameter
     * @param requestType router request type
     * @return if is special return true
     */
    public boolean doHandleParam(RouterRequestValues parameters, ExecutableArgument invokedParameter, MediaType requestType) {
        boolean result = false;

        Map<String, List> mixValues = parameters.getMixValues();

        var dataValidateFactory = new DefaultDataValidateFactory();

        ExecutableArgumentResolverFactory factory = new ExecutableArgumentResolverFactory();
        ExecutableArgumentResolver resolver = factory.factory(invokedParameter);
        if (resolver instanceof RequestParameterResolver) {
            RequestParameterResolver requestParameterResolver = (RequestParameterResolver) resolver;
            requestParameterResolver.setRequestType(requestType);
            result = requestParameterResolver.resolveArgument(invokedParameter, parameters, dataValidateFactory);
        } else if (resolver != null) {
            if ("com.truthbean.debbie.jdbc.domain.PageableRouterMethodArgumentResolver".equals(resolver.getClass().getName())) {
                result = resolver.resolveArgument(invokedParameter, parameters.getQueries(), dataValidateFactory);
            } else {
                result = resolver.resolveArgument(invokedParameter, parameters, dataValidateFactory);
            }
        } else {
            if (!mixValues.isEmpty()) {
                LOGGER.debug("mixValues: " + mixValues);
                handleParam(mixValues, invokedParameter);
            } else {
                LOGGER.debug("args is empty");
            }
        }
        return result;
    }

    public void handle(RouterRequestValues parameters, ExecutableArgument invokedParameter, MediaType requestType) {
        if (doHandleParam(parameters, invokedParameter, requestType)) return;

        if (invokedParameter.getValue() == null) {
            handleFields(parameters, invokedParameter, requestType);
        }

        /*if (TypeHelper.isBaseType(invokedParameter.getType())
                || invokedParameter.getType() == MultipartFile.class
                || TypeHelper.isArrayType(invokedParameter.getType())) {
            LOGGER.debug("type is base type");
            doHandleParam(parameters, invokedParameter);
        }*/
    }

    public void handleFields(RouterRequestValues parameters, ExecutableArgument invokedParameter, MediaType requestType) {
        Class<?> parameterType = invokedParameter.getType();
        List<Field> fields = ReflectionHelper.getDeclaredFields(parameterType);
        Object instance = ReflectionHelper.newInstance(parameterType);
        int i = 0;
        while (i < fields.size()) {
            ExecutableArgument parameter = typeOf(fields.get(i), i);
            var type = parameter.getType();
            if (!TypeHelper.isBaseType(type) && type != MultipartFile.class &&
                    !TypeHelper.isAbstractOrInterface(type) && TypeHelper.hasDefaultConstructor(type)
                    && !type.getName().equals(parameterType.getName())) {
                try {
                    Object newInstance = ReflectionHelper.newInstance(type);
                    handleInstance(parameters, newInstance, parameter);
                    assert instance != null;
                    ReflectionHelper.invokeSetMethod(instance, fields.get(i), newInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                doHandleFiled(parameters, instance, fields.get(i), parameter, requestType);
            }

            i++;
        }
        invokedParameter.setValue(instance);
        doHandleParam(parameters, invokedParameter, requestType);
    }

    public void doHandleFiled(RouterRequestValues parameters, Object instance, Field field,
                              ExecutableArgument invokedParameter, MediaType requestType) {
        Map<String, List> mixValues = parameters.getMixValues();

        Annotation annotation = invokedParameter.getAnnotation(RequestParameter.class);
        if (annotation != null) {
            RequestParameter requestParameter = (RequestParameter) annotation;

            switch (requestParameter.paramType()) {
                case MIX:
                    if (!mixValues.isEmpty()) {
                        handleFiled(mixValues, instance, field, invokedParameter);
                    }
                    break;
                case QUERY:
                    Map<String, List> queries = parameters.getQueries();
                    if (queries != null && !queries.isEmpty()) {
                        handleFiled(queries, instance, field, invokedParameter);
                    }
                    break;
                case PATH:
                    Map<String, List> paths = parameters.getPathAttributes();
                    if (paths != null && !paths.isEmpty()) {
                        handleFiled(paths, instance, field, invokedParameter);
                    }
                    break;
                case MATRIX:
                    Map<String, List> matrix = parameters.getMatrixAttributes();
                    if (matrix != null && !matrix.isEmpty()) {
                        handleFiled(matrix, instance, field, invokedParameter);
                    }
                    break;
                case PARAM:
                    Map<String, List> params = parameters.getParams();
                    if (params != null && !params.isEmpty()) {
                        handleFiled(params, instance, field, invokedParameter);
                    }
                    break;
                case BODY:
                    var type = requestParameter.bodyType();
                    if (type == MediaType.ANY) {
                        type = requestType;
                    }
                    handleObjectFiled(parameters.getBody(), type, instance, field, invokedParameter);
                    break;
                case HEAD:
                    Map<String, List> headers = parameters.getHeaders();
                    if (headers != null && !headers.isEmpty()) {
                        handleFiled(headers, instance, field, invokedParameter);
                    }
                    break;
                case COOKIE:
                    Map<String, List> cookieAttributes = parameters.getCookieAttributes();
                    if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
                        handleFiled(cookieAttributes, instance, field, invokedParameter);
                    }
                    break;
                case SESSION:
                    Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
                    if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
                        handleObject(sessionAttributes, invokedParameter);
                    }
                    break;
                case INNER:
                    Map<String, Object> requestAttributes = parameters.getInnerAttributes();
                    if (requestAttributes != null && !requestAttributes.isEmpty()) {
                        handleObject(requestAttributes, invokedParameter);
                    }
                    break;
                default:
                    break;
            }
        } else {
            handleFiled(mixValues, instance, field, invokedParameter);
        }
    }

    public static List<ExecutableArgument> typeOf(Parameter[] parameters) {
        List<ExecutableArgument> result = new ArrayList<>();

        ExecutableArgument invokedParameter;
        for (Parameter parameter : parameters) {
            invokedParameter = new ExecutableArgument();
            invokedParameter.setType(parameter.getType());
            if (!parameter.isNamePresent()) {
                String name = parameter.getName();
                if (name.startsWith("arg")) {
                    invokedParameter.setIndex(Integer.valueOf(name.split("arg")[1]));
                } else {
                    invokedParameter.setName(name);
                }
            }
            RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
            if (requestParameter != null) {
                invokedParameter.setName(requestParameter.name());
            }
            invokedParameter.setAnnotations(parameter.getAnnotations());

            result.add(invokedParameter);
        }
        Collections.sort(result);
        return result;
    }
}
