package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.io.MultipartFile;
import com.truthbean.debbie.core.reflection.InvokedParameter;
import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.core.reflection.TypeHelper;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.mvc.response.view.AbstractView;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author TruthBean
 * Created on 2018-04-08 10:05.
 * @since 0.0.1
 */
public class MvcRouterInvokedParameterHandler extends AbstractInvokedParameterHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterInvokedParameterHandler.class);

    public List handleMethodParams(RouterRequestValues parameters, List<InvokedParameter> methodParams, MediaType requestType) {
        List<Object> result = new LinkedList<>();
        for (InvokedParameter invokedParameter : methodParams) {
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

        for (InvokedParameter invokedParameter : methodParams) {
            result.add(invokedParameter.getValue());
        }

        return result;
    }

    private static boolean isTemplateView(Class<?> clazz) {
        return clazz == AbstractTemplateView.class;
    }

    public void handleInstance(RouterRequestValues parameters, Object newInstance, InvokedParameter invokedParameter) {
        if (invokedParameter.getType() == AbstractView.class) {
            invokedParameter.setValue(new StaticResourcesView());
            return;
        }

        if (TypeHelper.isBaseType(invokedParameter.getType())
                || invokedParameter.getType() == MultipartFile.class) {

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
                InvokedParameter parameter = typeOf(fields.get(i), i);
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
    public boolean doHandleParam(RouterRequestValues parameters, InvokedParameter invokedParameter, MediaType requestType) {
        boolean result = false;

        Map<String, List> mixValues = parameters.getMixValues();

        Annotation annotation = invokedParameter.getAnnotation();
        if (annotation instanceof RequestParameter) {
            LOGGER.debug("annotation is RequestParameter");
            RequestParameter requestParameter = (RequestParameter) annotation;

            switch (requestParameter.paramType()) {
                case MIX:
                    handleParam(mixValues, invokedParameter);
                    break;
                case QUERY:
                    handleParam(parameters.getQueries(), invokedParameter);
                    break;
                case PATH:
                    handleParam(parameters.getPathAttributes(), invokedParameter);
                    break;
                case MATRIX:
                    Map<String, List> matrix = parameters.getMatrixAttributes();
                    handleParam(matrix, invokedParameter);
                    break;
                case PARAM:
                    Map<String, List> params = parameters.getParams();
                    handleParam(params, invokedParameter);
                    break;
                case BODY:
                    var type = requestParameter.bodyType();
                    if (type == MediaType.ANY) {
                        type = requestType;
                    }
                    String textBody = parameters.getTextBody();
                    if (textBody == null) {
                        handleBody(parameters.getBody(), type, invokedParameter);
                    } else {
                        handleBody(textBody, type, invokedParameter);
                    }
                    break;
                case HEAD:
                    Map<String, List> headers = parameters.getHeaders();
                    handleParam(headers, invokedParameter);
                    break;
                case COOKIE:
                    Map<String, List> cookieAttributes = parameters.getCookieAttributes();
                    handleParam(cookieAttributes, invokedParameter);
                    break;
                case SESSION:
                    Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
                    handleObject(sessionAttributes, invokedParameter);
                    result = true;
                    break;
                case INNER:
                    Map<String, Object> requestAttributes = parameters.getInnerAttributes();
                    handleObject(requestAttributes, invokedParameter);
                    result = true;
                    break;
                default:
                    break;
            }

            if (requestParameter.require() && invokedParameter.getValue() == null) {
                throw new IllegalArgumentException(requestParameter.name() + " has no value! ");
            }

            if (!requestParameter.require() && invokedParameter.getValue() == null) {
                handleParam(invokedParameter.getName(), requestParameter.defaultValue(), invokedParameter);
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

    public void handle(RouterRequestValues parameters, InvokedParameter invokedParameter, MediaType requestType) {
        if (doHandleParam(parameters, invokedParameter, requestType)) return;

        if (invokedParameter.getValue() == null) {
            List<Field> fields = ReflectionHelper.getDeclaredFields(invokedParameter.getType());
            Object instance = ReflectionHelper.newInstance(invokedParameter.getType());
            int i = 0;
            while (i < fields.size()) {
                InvokedParameter parameter = typeOf(fields.get(i), i);
                if (!TypeHelper.isBaseType(parameter.getType()) && parameter.getType() != MultipartFile.class) {
                    try {
                        Object newInstance = parameter.getType().getDeclaredConstructor().newInstance();
                        handleInstance(parameters, newInstance, parameter);
                        assert instance != null;
                        ReflectionHelper.invokeSetMethod(instance, fields.get(i), newInstance);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                            | InvocationTargetException e) {
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

        /*if (TypeHelper.isBaseType(invokedParameter.getType())
                || invokedParameter.getType() == MultipartFile.class
                || TypeHelper.isArrayType(invokedParameter.getType())) {
            LOGGER.debug("type is base type");
            doHandleParam(parameters, invokedParameter);
        }*/
    }

    public void doHandleFiled(RouterRequestValues parameters, Object instance, Field field,
                              InvokedParameter invokedParameter, MediaType requestType) {
        Map<String, List> mixValues = parameters.getMixValues();

        Annotation annotation = invokedParameter.getAnnotation();
        if (annotation instanceof RequestParameter) {
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

    public static List<InvokedParameter> typeOf(Parameter[] parameters) {
        List<InvokedParameter> result = new ArrayList<>();

        InvokedParameter invokedParameter;
        for (Parameter parameter : parameters) {
            invokedParameter = new InvokedParameter();
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
            invokedParameter.setAnnotation(requestParameter);

            result.add(invokedParameter);
        }
        Collections.sort(result);
        return result;
    }
}
