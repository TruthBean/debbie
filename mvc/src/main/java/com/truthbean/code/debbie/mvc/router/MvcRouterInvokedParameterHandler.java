package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.io.MultipartFile;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import com.truthbean.code.debbie.core.reflection.TypeHelper;
import com.truthbean.code.debbie.mvc.RouterSession;
import com.truthbean.code.debbie.mvc.request.RequestParam;
import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.code.debbie.mvc.response.view.AbstractView;
import com.truthbean.code.debbie.mvc.response.view.StaticResourcesView;
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

    public List handleMethodParams(RouterRequestValues parameters, List<InvokedParameter> methodParams) {
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
            handle(parameters, invokedParameter);
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

            Map<String, List> queries = parameters.getQueries();
            if (queries != null && !queries.isEmpty()) {
                handleParam(queries, invokedParameter);
            }

            Map<String, List> params = parameters.getParams();
            if (params != null && !params.isEmpty()) {
                handleParam(params, invokedParameter);
            }

            Map<String, List> headers = parameters.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                handleParam(headers, invokedParameter);
            }

            Map<String, List> pathAttributes = parameters.getPathAttributes();
            if (pathAttributes != null && !pathAttributes.isEmpty()) {
                handleParam(pathAttributes, invokedParameter);
            }

            Map<String, List> cookieAttributes = parameters.getCookieAttributes();
            if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
                handleParam(cookieAttributes, invokedParameter);
            }

            Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
            if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
                handleSession(sessionAttributes, invokedParameter);
            }

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
                        // handleSession(sessionAttributes,newInstance, fields.get(i), parameter);
                    }
                }
            }
            i++;
        }
    }

    public void doHandleParam(RouterRequestValues parameters, InvokedParameter invokedParameter) {
        Map<String, List> mixValues = parameters.getMixValues();

        Annotation annotation = invokedParameter.getAnnotation();
        if (annotation instanceof RequestParam) {
            LOGGER.debug("annotation is RequestParam");
            RequestParam requestParam = (RequestParam) annotation;

            switch (requestParam.paramType()) {
                case MIX:
                    if (!mixValues.isEmpty()) {
                        handleParam(mixValues, invokedParameter);
                    }
                    break;
                case QUERY:
                    Map<String, List> queries = parameters.getQueries();
                    if (queries != null && !queries.isEmpty()) {
                        handleParam(queries, invokedParameter);
                    }
                    break;
                case PARAM:
                    Map<String, List> params = parameters.getParams();
                    if (params != null && !params.isEmpty()) {
                        handleParam(params, invokedParameter);
                    }
                    break;
                case BODY:
                    handleBody(parameters.getBody(), requestParam.bodyType(), invokedParameter);
                    break;
                case HEAD:
                    Map<String, List> headers = parameters.getHeaders();
                    if (headers != null && !headers.isEmpty()) {
                        handleParam(headers, invokedParameter);
                    }
                    break;
                case COOKIE:
                    Map<String, List> cookieAttributes = parameters.getCookieAttributes();
                    if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
                        handleParam(cookieAttributes, invokedParameter);
                    }
                    break;
                case SESSION:
                    Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
                    if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
                        handleSession(sessionAttributes, invokedParameter);
                    }
                    break;
                default:
                    break;
            }

            if (!requestParam.require() && invokedParameter.getValue() == null) {
                handleParam(invokedParameter.getName(), requestParam.defaultValue(), invokedParameter);
            }
        } else {
            if (!mixValues.isEmpty()) {
                LOGGER.debug("mixValues: " + mixValues);
                handleParam(mixValues, invokedParameter);
            } else {
                LOGGER.debug("args is empty");
            }
        }
    }

    public void handle(RouterRequestValues parameters, InvokedParameter invokedParameter) {
        if (TypeHelper.isBaseType(invokedParameter.getType())
                || invokedParameter.getType() == MultipartFile.class
                || TypeHelper.isArrayType(invokedParameter.getType())) {
            LOGGER.debug("type is base type");
            doHandleParam(parameters, invokedParameter);
        } else {
            List<Field> fields = ReflectionHelper.getDeclaredFields(invokedParameter.getType());
            Object instance = null;
            try {
                instance = invokedParameter.getType().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
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
                    doHandleFiled(parameters, instance, fields.get(i), parameter);
                }

                i++;
            }
            invokedParameter.setValue(instance);
            doHandleParam(parameters, invokedParameter);
        }
    }

    public void doHandleFiled(RouterRequestValues parameters, Object instance, Field field, InvokedParameter invokedParameter) {
        Map<String, List> mixValues = parameters.getMixValues();

        Annotation annotation = invokedParameter.getAnnotation();
        if (annotation != null && annotation instanceof RequestParam) {
            RequestParam requestParam = (RequestParam) annotation;

            switch (requestParam.paramType()) {
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
                case PARAM:
                    Map<String, List> params = parameters.getParams();
                    if (params != null && !params.isEmpty()) {
                        handleFiled(params, instance, field, invokedParameter);
                    }
                    break;
                case BODY:
                    handleObjectFiled(parameters.getBody(), requestParam.bodyType(), instance,
                            field, invokedParameter);
                    break;
                case HEAD:
                    Map<String, List> headers = parameters.getHeaders();
                    if (headers != null && !headers.isEmpty()) {
                        handleParam(headers, invokedParameter);
                    }
                    break;
                case COOKIE:
                    Map<String, List> cookieAttributes = parameters.getCookieAttributes();
                    if (cookieAttributes != null && !cookieAttributes.isEmpty()) {
                        handleParam(cookieAttributes, invokedParameter);
                    }
                    break;
                case SESSION:
                    Map<String, Object> sessionAttributes = parameters.getSessionAttributes();
                    if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
                        handleSession(sessionAttributes, invokedParameter);
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
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                invokedParameter.setName(requestParam.name());
            }
            invokedParameter.setAnnotation(requestParam);

            result.add(invokedParameter);
        }
        Collections.sort(result);
        return result;
    }
}
