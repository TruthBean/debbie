/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.data.validate.DefaultDataValidateFactory;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.jackson.data.JacksonJsonHelper;
import com.truthbean.debbie.jackson.data.JacksonXmlHelper;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RequestParameterInfo;
import com.truthbean.debbie.mvc.request.RequestParameterResolver;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.mvc.response.view.AbstractView;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import com.truthbean.debbie.reflection.*;
import com.truthbean.core.util.StringUtils;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author TruthBean
 * Created on 2018-04-08 10:05.
 * @since 0.0.1
 */
public class RouterMethodArgumentHandler extends ExecutableArgumentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterMethodArgumentHandler.class);

    public RouterMethodArgumentHandler(ClassLoader classLoader) {
        super(classLoader, new JacksonJsonHelper(), new JacksonXmlHelper());
    }

    public List<Object> handleMethodParams(RouterRequestValues parameters, List<ExecutableArgument> methodParams, MediaType requestType) {
        List<Object> result = new LinkedList<>();
        for (ExecutableArgument invokedParameter : methodParams) {
            LOGGER.debug(() -> "invokedParameter " + invokedParameter.getType().getTypeName());
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
            handleParam(parameters.getQueries(), invokedParameter, false);
            // param
            // handleParam(parameters.getParams(), invokedParameter, false);
            // header
            handleParam(parameters.getHeaders(), invokedParameter, true);
            // path
            handleParam(parameters.getPathAttributes(), invokedParameter, false);
            // cookie
            handleObjectParam(parameters.getCookieAttributes(), invokedParameter, true);
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
                    Map<String, List<String>> queries = parameters.getQueries();
                    if (queries != null && !queries.isEmpty()) {
                        handleFiled(queries, newInstance, fields.get(i), parameter, false);
                    }
                    // Map<String, List> params = parameters.getParams();
//                    if (params != null && !params.isEmpty()) {
//                        handleFiled(params, newInstance, fields.get(i), parameter, false);
//                    }
                    if (parameters.getBody() != null) {
                                /*handleObjectFiled(parameters.getBody(), requestParam.requestType(), newInstance,
                                        fields.get(i), parameter);*/
                        // todo
                    }

                    Map<String, List<String>> headers = parameters.getHeaders();
                    if (headers != null && !headers.isEmpty()) {
                        handleFiled(headers, newInstance, fields.get(i), parameter, true);
                    }
                    Map<String, List<Object>> cookieAttributes = parameters.getCookieAttributes();
                    if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
                        handleObjectFiled(cookieAttributes, newInstance, fields.get(i), parameter, true);
                    }
                    Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
                    if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
                        // handleFiled(sessionAttributes,newInstance, fields.get(i), parameter);
                        // todo
                    }
                }
                i++;
            }
        }
    }

    /**
     * @param parameters       router request values
     * @param invokedParameter method parameter
     * @param requestType      router request type
     * @return if is special return true
     */
    public boolean doHandleParam(RouterRequestValues parameters, ExecutableArgument invokedParameter, MediaType requestType) {
        boolean result = false;

        Map<String, List<Object>> mixValues = parameters.getMixValues();

        var dataValidateFactory = new DefaultDataValidateFactory();

        ExecutableArgumentResolverFactory factory = new ExecutableArgumentResolverFactory();
        ExecutableArgumentResolver resolver = factory.factory(invokedParameter);
        if (resolver != null) {
            String resolverClassName = resolver.getClass().getName();
            if ("com.truthbean.debbie.jdbc.domain.PageableHandlerMethodArgumentResolver".equals(resolverClassName) ||
                    "com.truthbean.debbie.jdbc.domain.PageableRouterMethodArgumentResolver".equals(resolverClassName) ||
                    "com.truthbean.debbie.jdbc.domain.SortHandlerMethodArgumentResolver".equals(resolverClassName)) {
                result = resolver.resolveArgument(invokedParameter, parameters.getQueries(), dataValidateFactory);
            } else if (resolver instanceof RequestParameterResolver) {
                RequestParameterResolver requestParameterResolver = (RequestParameterResolver) resolver;
                requestParameterResolver.setRequestType(requestType);
                result = requestParameterResolver.resolveArgument(invokedParameter, parameters, dataValidateFactory);
            } else {
                result = resolver.resolveArgument(invokedParameter, parameters, dataValidateFactory);
            }
        } else {
            if (!mixValues.isEmpty()) {
                LOGGER.debug(() -> "mixValues: " + mixValues);
                handleObjectParam(mixValues, invokedParameter, false);
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
        Class<?> parameterType = invokedParameter.getRawType();
        Object instance = ReflectionHelper.newInstance(parameterType);
        List<Field> fields = ReflectionHelper.getDeclaredFields(parameterType);
        int i = 0;
        while (i < fields.size()) {
            ExecutableArgument parameter = typeOf(fields.get(i), i);
            var type = parameter.getRawType();
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
        Map<String, List<Object>> mixValues = parameters.getMixValues();

        RequestParameterInfo requestParameter = RequestParameterInfo.fromExecutableArgumentAnnotation(invokedParameter);
        if (requestParameter != null) {

            switch (requestParameter.paramType()) {
                case MIX:
                    if (!mixValues.isEmpty()) {
                        handleObjectFiled(mixValues, instance, field, invokedParameter, false);
                    }
                    break;
                case QUERY:
                    Map<String, List<String>> queries = parameters.getQueries();
                    if (queries != null && !queries.isEmpty()) {
                        handleFiled(queries, instance, field, invokedParameter, false);
                    }
                    break;
                case PATH:
                    Map<String, List<String>> paths = parameters.getPathAttributes();
                    if (paths != null && !paths.isEmpty()) {
                        handleFiled(paths, instance, field, invokedParameter, false);
                    }
                    break;
                case MATRIX:
                    Map<String, List<String>> matrix = parameters.getMatrixAttributes();
                    if (matrix != null && !matrix.isEmpty()) {
                        handleFiled(matrix, instance, field, invokedParameter, false);
                    }
                    break;
                case PARAM:
                    /*Map<String, List> params = parameters.getParams();
                    if (params != null && !params.isEmpty()) {
                        handleFiled(params, instance, field, invokedParameter, false);
                    }*/
                    break;
                case BODY:
                    var type = requestParameter.bodyType();
                    if (type == MediaType.ANY) {
                        type = requestType;
                    }
                    handleObjectFiled(parameters.getBody(), type, instance, field, invokedParameter);
                    break;
                case HEAD:
                    Map<String, List<String>> headers = parameters.getHeaders();
                    if (headers != null && !headers.isEmpty()) {
                        handleFiled(headers, instance, field, invokedParameter, true);
                    }
                    break;
                case COOKIE:
                    Map<String, List<Object>> cookieAttributes = parameters.getCookieAttributes();
                    if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
                        handleObjectFiled(cookieAttributes, instance, field, invokedParameter, true);
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
            handleObjectFiled(mixValues, instance, field, invokedParameter, false);
        }
    }

    public static List<ExecutableArgument> typeOf(Method method, Class<?> declaringClass, ClassLoader classLoader) {
        List<ExecutableArgument> result = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        Type[] parameterTypes = ReflectionHelper.getMethodActualTypes(method, declaringClass);

        ExecutableArgument invokedParameter;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            invokedParameter = new ExecutableArgument(classLoader);
            Type parameterizedType = parameterTypes[i];
            invokedParameter.setType(parameterizedType);
            invokedParameter.setIndex(i);

            RequestParameterInfo requestParameter = RequestParameterInfo.fromParameterAnnotation(parameter);
            if (requestParameter != null) {
                var name = requestParameter.value();
                if (name.isBlank()) {
                    name = requestParameter.name();
                }
                invokedParameter.setName(name);
            }
            invokedParameter.setAnnotations(parameter.getAnnotations());

            String name = invokedParameter.getName();
            if (!StringUtils.hasText(name)) {
                if (parameter.isNamePresent()) {
                    invokedParameter.setName(parameter.getName());
                }
            }
            invokedParameter.setStack("param(" + parameterizedType + "[" + i + "]" + name + ") \nin method(" + method.toString() + ")\n");

            result.add(invokedParameter);
        }
        Collections.sort(result);
        return result;
    }
}
