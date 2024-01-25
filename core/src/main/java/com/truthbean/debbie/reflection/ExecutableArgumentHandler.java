/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.data.JsonHelper;
import com.truthbean.debbie.data.XmlHelper;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.core.util.StringUtils;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @see java.lang.reflect.Executable
 * @see ExecutableArgument
 *
 * @author TruthBean
 * @since 0.0.2
 * Created on 2018-03-10 13:32
 */
public class ExecutableArgumentHandler {

    private final ClassLoader classLoader;
    private final JsonHelper jsonHelper;
    private final XmlHelper xmlHelper;

    public ExecutableArgumentHandler(ClassLoader classLoader, JsonHelper jsonHelper, XmlHelper xmlHelper) {
        this.classLoader = classLoader;
        this.jsonHelper = jsonHelper;
        this.xmlHelper = xmlHelper;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ExecutableArgument typeOf(Field field, int index) {
        ExecutableArgument invokedParameter = new ExecutableArgument(classLoader);
        Type genericType = field.getGenericType();
        if (genericType instanceof Class) {
            invokedParameter.setType(genericType);
        } else if (genericType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) field.getGenericType()).getRawType();
            invokedParameter.setType(rawType);
        }
        invokedParameter.setName(field.getName());
        invokedParameter.setIndex(index);
        return invokedParameter;
    }

    public static List<ExecutableArgument> typeOf(Method method, GlobalBeanFactory beanFactory, ClassLoader classLoader) {
        List<ExecutableArgument> result = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();

        ExecutableArgument invokedParameter;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            invokedParameter = new ExecutableArgument(classLoader);
            Class<?> type = parameterTypes[i];
            invokedParameter.setType(type);
            invokedParameter.setIndex(i);

            BeanInject beanInject = parameter.getAnnotation(BeanInject.class);
            if (beanInject != null) {
                var name = beanInject.value();
                if (name.isBlank()) {
                    name = beanInject.name();
                }

                Object value;
                if (!name.isBlank()) {
                    invokedParameter.setName(name);

                    value = beanFactory.factory(name);
                } else {
                    value = beanFactory.factory(type);
                }
                invokedParameter.setValue(value);
            }

            String name = invokedParameter.getName();
            if (!StringUtils.hasText(name) && parameter.isNamePresent()) {
                invokedParameter.setName(parameter.getName());
            }

            invokedParameter.setAnnotations(parameter.getAnnotations());

            result.add(invokedParameter);
        }
        Collections.sort(result);
        return result;
    }

    public void handleObjectFiled(Map<String, List<Object>> map,
                            Object newInstance, Field field, ExecutableArgument invokedParameter,
                            boolean ignoreCase) {

        if (map == null || map.isEmpty()) {
            return;
        }
        if (newInstance == null) {
            handleObjectParam(map, invokedParameter, ignoreCase);
        } else {
            if (field.getName().equals(invokedParameter.getName()) && map.containsKey(field.getName())) {
                handleObjectParam(map, invokedParameter, ignoreCase);
                if (invokedParameter.getValue() != null) {
                    //set value to filed
                    ReflectionHelper.invokeSetMethod(newInstance, field.getName(), invokedParameter.getValue(),
                            invokedParameter.getRawType());
                }
            }
        }
    }

    public void handleFiled(Map<String, List<String>> map,
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

    public static Object handleObjectParam(Map<String, List<Object>> map, String name, Type type, boolean ignoreCase) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
                boolean flag;
                if (ignoreCase) {
                    flag = (name == null || name.equalsIgnoreCase(entry.getKey())) &&
                            TypeHelper.isOrValueOf(type, entry.getValue());
                } else {
                    flag = (name == null || name.equals(entry.getKey())) &&
                            TypeHelper.isOrValueOf(type, entry.getValue());
                }

                if (flag) {
                    Object value;
                    if (TypeHelper.isArrayType(type)) {
                        value = TypeHelper.valueOf(type, entry.getValue());
                    } else {
                        value = TypeHelper.valueOf(type, entry.getValue().get(0));
                    }
                    return value;
                }
            }
        }
        return null;
    }

    public void handleObjectParam(Map<String, List<Object>> map, ExecutableArgument invokedParameter, boolean ignoreCase) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
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

    public void handleParam(Map<String, List<String>> map, ExecutableArgument invokedParameter, boolean ignoreCase) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
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
        Object value = null;
        Class<?> clazz = TypeHelper.getClass(invokedParameter.getType());
        switch (type) {
            case APPLICATION_XML:
            case APPLICATION_XML_UTF8:
                if (clazz == List.class) {
                    try {
                        Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                        value = xmlHelper.xmlStreamToListBean(stream, (Class<?>)actualType[0]);
                        if (value == null) {
                            value = new ArrayList<>();
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        value = new ArrayList<>();
                    }
                } else if (clazz == Set.class) {
                    try {
                        Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                        value = xmlHelper.xmlStreamToSetBean(stream, (Class<?>)actualType[0]);
                        if (value == null) {
                            value = new HashSet<>();
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        value = new HashSet<>();
                    }
                } else if (clazz != null) {
                    try {
                        value = xmlHelper.xmlStreamToBean(stream, clazz);
                        if (value == null) {
                            value = ReflectionHelper.newInstance(clazz);
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        value = ReflectionHelper.newInstance(clazz);
                    }
                }
                break;
            case APPLICATION_JSON:
            case APPLICATION_JSON_UTF8:
                if (clazz == List.class) {
                    try {
                        Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                        value = jsonHelper.jsonStreamToListBean(stream, (Class<?>)actualType[0]);
                        if (value == null) {
                            value = new ArrayList<>();
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        value = new ArrayList<>();
                    }
                } else if (clazz == Set.class) {
                    try {
                        Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                        value = jsonHelper.jsonStreamToSetBean(stream, (Class<?>)actualType[0]);
                        if (value == null) {
                            value = new HashSet<>();
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        value = new HashSet<>();
                    }
                } else if (clazz != null) {
                    try {
                        value = jsonHelper.jsonStreamToBean(stream, clazz);
                        if (value == null) {
                            value = ReflectionHelper.newInstance(clazz);
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        value = ReflectionHelper.newInstance(clazz);
                    }
                }
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
                if (clazz == List.class) {
                    Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                    value = xmlHelper.xmlToListBean(body, (Class<?>)actualType[0]);
                } else if (clazz == Set.class) {
                    Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                    value = xmlHelper.xmlToSetBean(body, (Class<?>)actualType[0]);
                } else {
                    value = xmlHelper.xmlToBean(body, clazz);
                }
                break;
            case APPLICATION_JSON:
            case APPLICATION_JSON_UTF8:
                if (clazz == List.class) {
                    Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                    value = jsonHelper.jsonToListBean(body, (Class<?>)actualType[0]);
                } else if (clazz == Set.class) {
                    Type[] actualType = TypeHelper.getActualType(invokedParameter.getType());
                    value = jsonHelper.jsonToCollectionBean(body, Set.class, (Class<?>)actualType[0]);
                } else {
                    value = jsonHelper.jsonToBean(body, clazz);
                }
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

    public static ExecutableArgument typeOf(Parameter parameter, ClassLoader classLoader) {
        ExecutableArgument invokedParameter = new ExecutableArgument(classLoader);
        invokedParameter.setType(parameter.getParameterizedType());
        String name = parameter.getName();
        if (name.startsWith("arg")) {
            invokedParameter.setIndex(Integer.parseInt(name.split("arg")[1]));
        }
        return invokedParameter;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutableArgumentHandler.class);
}