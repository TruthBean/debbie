package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import com.truthbean.code.debbie.core.reflection.TypeHelper;
import com.truthbean.code.debbie.core.util.JacksonUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-10 13:32
 */
public abstract class AbstractInvokedParameterHandler {

    public InvokedParameter typeOf(Field field, int index) {
        InvokedParameter invokedParameter = new InvokedParameter();
        invokedParameter.setType(field.getType());
        invokedParameter.setName(field.getName());
        invokedParameter.setIndex(index);
        return invokedParameter;
    }

    public void handleFiled(Map<String, List> map,
                            Object newInstance, Field field, InvokedParameter invokedParameter) {

        if (map == null || map.isEmpty()) {
            return;
        }
        if (newInstance == null) {
            handleParam(map, invokedParameter);
        } else {
            if (field.getName().equals(invokedParameter.getName()) && map.containsKey(field.getName())) {
                handleParam(map, invokedParameter);
                if (invokedParameter.getValue() != null) {
                    //set value to filed
                    ReflectionHelper.invokeSetMethod(newInstance, field.getName(), invokedParameter.getValue(),
                            invokedParameter.getType());
                }
            }
        }

    }

    public void handleObjectFiled(InputStream inputStream, MediaType type,
                                  Object newInstance, Field field, InvokedParameter invokedParameter) {

        if (inputStream == null) {
            return;
        }
        if (newInstance == null) {
            handleBody(inputStream, type, invokedParameter);
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

    public void handleParam(Map<String, List> map, InvokedParameter invokedParameter) {
        for (Map.Entry<String, List> entry : map.entrySet()) {
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

    public void handleSession(Map<String, Object> map, InvokedParameter invokedParameter) {
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

    public void handleBody(InputStream stream, MediaType type, InvokedParameter invokedParameter) {
        Object value;
        switch (type) {
            case APPLICATION_XML:
            case APPLICATION_XML_UTF8:
                value = JacksonUtils.xmlStreamToBean(stream, invokedParameter.getType());
                break;
            case APPLICATION_JSON:
            case APPLICATION_JSON_UTF8:
                value = JacksonUtils.jsonStreamToBean(stream, invokedParameter.getType());
                break;
            // TODO MORE CASE
            default:
                value = stream;
                break;
        }
        invokedParameter.setValue(value);
    }

    public void handleParamWithStringValue(Map<String, List<String>> map, InvokedParameter invokedParameter) {
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

    public void handleParam(String name, String rawValue, InvokedParameter invokedParameter) {
        Object value;
        if (TypeHelper.isArrayType(invokedParameter.getType())) {
            value = TypeHelper.valueOf(invokedParameter.getType(), rawValue);
        } else {
            value = TypeHelper.valueOf(invokedParameter.getType(), rawValue);
        }
        invokedParameter.setName(name);
        invokedParameter.setValue(value);
    }

    public static InvokedParameter typeOf(Parameter parameter) {
        InvokedParameter invokedParameter = new InvokedParameter();
        invokedParameter.setType(parameter.getType());
        String name = parameter.getName();
        if (name.startsWith("arg")) {
            invokedParameter.setIndex(Integer.valueOf(name.split("arg")[1]));
        }
        return invokedParameter;
    }
}