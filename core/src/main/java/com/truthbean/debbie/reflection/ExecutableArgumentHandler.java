package com.truthbean.debbie.reflection;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.debbie.util.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @see java.lang.reflect.Executable
 * @see ExecutableArgument
 *
 * @author TruthBean
 * @since 0.0.2
 * Created on 2018-03-10 13:32
 */
public class ExecutableArgumentHandler {

    public ExecutableArgument typeOf(Field field, int index) {
        ExecutableArgument invokedParameter = new ExecutableArgument();
        Type genericType = field.getGenericType();
        if (genericType instanceof Class) {
            invokedParameter.setType((Class<?>) genericType);
        } else if (genericType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) field.getGenericType()).getRawType();
            invokedParameter.setType((Class<?>) rawType);
        }
        invokedParameter.setName(field.getName());
        invokedParameter.setIndex(index);
        return invokedParameter;
    }

    public static List<ExecutableArgument> typeOf(Method method, BeanFactoryHandler beanFactoryHandler) {
        List<ExecutableArgument> result = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();

        ExecutableArgument invokedParameter;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            invokedParameter = new ExecutableArgument();
            Class<?> type = parameterTypes[i];
            invokedParameter.setType(type);
            if (!parameter.isNamePresent()) {
                String name = parameter.getName();
                if (name.startsWith("arg")) {
                    invokedParameter.setIndex(Integer.parseInt(name.split("arg")[1]));
                } else {
                    invokedParameter.setName(name);
                }
            }

            BeanInject beanInject = parameter.getAnnotation(BeanInject.class);
            if (beanInject != null) {
                var name = beanInject.value();
                if (name.isBlank()) {
                    name = beanInject.name();
                }

                Object value;
                if (!name.isBlank()) {
                    invokedParameter.setName(name);

                    value = beanFactoryHandler.factory(name);
                } else {
                    value = beanFactoryHandler.factory(type);
                }
                invokedParameter.setValue(value);
            }

            invokedParameter.setAnnotations(parameter.getAnnotations());

            result.add(invokedParameter);
        }
        Collections.sort(result);
        return result;
    }

    public void handleFiled(Map<String, List> map,
                            Object newInstance, Field field, ExecutableArgument invokedParameter,
                            boolean ignoreCase) {

        if (map == null || map.isEmpty()) {
            return;
        }
        if (newInstance == null) {
            handleParam(map, invokedParameter, ignoreCase);
        } else {
            if (field.getName().equals(invokedParameter.getName()) && map.containsKey(field.getName())) {
                handleParam(map, invokedParameter, ignoreCase);
                if (invokedParameter.getValue() != null) {
                    //set value to filed
                    ReflectionHelper.invokeSetMethod(newInstance, field.getName(), invokedParameter.getValue(),
                            invokedParameter.getRawType());
                }
            }
        }
    }

    public void handleObjectFiled(InputStream inputStream, MediaType type,
                                  Object newInstance, Field field, ExecutableArgument invokedParameter) {

        if (inputStream == null) {
            return;
        }
        if (newInstance == null) {
            handleStream(inputStream, type, invokedParameter);
        } else {
            /*if (field.getName().equals(invokedParameter.getName()) && map.containsKey(field.getName())) {
                handleParam(map, invokedParameter);
                if (invokedParameter.getValue() != null) {
                    //set value to filed
                    ReflectionHelper.invokeSetMethod(newInstance, field.getName(), invokedParameter.getValue(),
                            invokedParameter.getType());
                }
            }*/
        }

    }

    public void handleParam(Map<String, List> map, ExecutableArgument invokedParameter, boolean ignoreCase) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, List> entry : map.entrySet()) {
                boolean flag;
                if (ignoreCase) {
                    flag = (invokedParameter.getName() == null || invokedParameter.getName().equalsIgnoreCase(entry.getKey())) &&
                            TypeHelper.isOrValueOf(invokedParameter.getType(), entry.getValue());
                } else {
                    flag = (invokedParameter.getName() == null || invokedParameter.getName().equals(entry.getKey())) &&
                            TypeHelper.isOrValueOf(invokedParameter.getType(), entry.getValue());
                }

                if (flag) {
                    Object value;
                    if (TypeHelper.isArrayType(invokedParameter.getType())) {
                        value = TypeHelper.valueOf(invokedParameter.getType(), entry.getValue());
                    } else {
                        value = TypeHelper.valueOf(invokedParameter.getType(), entry.getValue().get(0));
                    }
                    invokedParameter.setName(entry.getKey());
                    invokedParameter.setValue(value);
                    break;
                }
            }
        }
    }

    public void handleObject(Map<String, Object> map, ExecutableArgument invokedParameter) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            boolean flag = (invokedParameter.getName() == null || invokedParameter.getName().equals(entry.getKey())) &&
                    TypeHelper.isOrValueOf(invokedParameter.getType(), entry.getValue());

            if (flag) {
                Object value = TypeHelper.valueOf(invokedParameter.getType(), entry.getValue());
                invokedParameter.setName(entry.getKey());
                invokedParameter.setValue(value);
                break;
            }
        }
    }

    public void handleStream(InputStream stream, MediaType type, ExecutableArgument invokedParameter) {
        Object value;
        Class<?> clazz = TypeHelper.getClass(invokedParameter.getType());
        switch (type) {
            case APPLICATION_XML:
            case APPLICATION_XML_UTF8:
                value = JacksonUtils.xmlStreamToBean(stream, clazz);
                break;
            case APPLICATION_JSON:
            case APPLICATION_JSON_UTF8:
                value = JacksonUtils.jsonStreamToBean(stream, clazz);
                break;
            case TEXT_PLAIN_UTF8:
            case TEXT_CSS_UTF8:
            case TEXT_HTML_UTF8:
            case TEXT_ANY_UTF8:
                try {
                    value = StreamHelper.copyToString(stream, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case TEXT_PLAIN:
            case TEXT_CSS:
            case TEXT_HTML:
            case TEXT_MARKDOWN:
            case TEXT_ANY:
                try {
                    value = StreamHelper.getAndClose(stream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            // TODO MORE CASE
            default:
                value = stream;
                break;
        }
        invokedParameter.setValue(value);
    }

    public void handleStream(String body, MediaType type, ExecutableArgument invokedParameter) {
        Object value;
        Class<?> clazz = TypeHelper.getClass(invokedParameter.getType());
        switch (type) {
            case APPLICATION_XML:
            case APPLICATION_XML_UTF8:
                value = JacksonUtils.xmlToBean(body, clazz);
                break;
            case APPLICATION_JSON:
            case APPLICATION_JSON_UTF8:
                value = JacksonUtils.jsonToBean(body, clazz);
                break;
            // TODO MORE CASE
            default:
                value = body;
                break;
        }
        invokedParameter.setValue(value);
    }

    public void handleParamWithStringValue(Map<String, List<String>> map, ExecutableArgument invokedParameter) {
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            boolean flag = (invokedParameter.getName() == null || invokedParameter.getName().equals(entry.getKey())) &&
                    TypeHelper.isOrValueOf(invokedParameter.getType(), entry.getValue());

            if (flag) {
                Object value;
                if (TypeHelper.isArrayType(invokedParameter.getType())) {
                    value = TypeHelper.valueOf(invokedParameter.getType(), entry.getValue());
                } else {
                    value = TypeHelper.valueOf(invokedParameter.getType(), entry.getValue().get(0));
                }
                invokedParameter.setName(entry.getKey());
                invokedParameter.setValue(value);
                break;
            }
        }
    }

    public void handleParam(String name, String rawValue, ExecutableArgument invokedParameter) {
        Object value;
        if (TypeHelper.isArrayType(invokedParameter.getType())) {
            value = TypeHelper.valueOf(invokedParameter.getType(), rawValue);
        } else {
            value = TypeHelper.valueOf(invokedParameter.getType(), rawValue);
        }
        invokedParameter.setName(name);
        invokedParameter.setValue(value);
    }

    public static ExecutableArgument typeOf(Parameter parameter) {
        ExecutableArgument invokedParameter = new ExecutableArgument();
        invokedParameter.setType((Class<?>) parameter.getParameterizedType());
        String name = parameter.getName();
        if (name.startsWith("arg")) {
            invokedParameter.setIndex(Integer.valueOf(name.split("arg")[1]));
        }
        return invokedParameter;
    }
}