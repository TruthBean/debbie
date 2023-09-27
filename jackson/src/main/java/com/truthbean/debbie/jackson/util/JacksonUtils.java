/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jackson.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthbean.Logger;
import com.truthbean.debbie.data.serialize.jackson.JacksonJsonUtils;
import com.truthbean.debbie.data.serialize.jackson.JacksonXmlUtils;
import com.truthbean.debbie.data.serialize.jackson.JacksonYamlUtils;
import com.truthbean.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created by TruthBean on 2017-08-02 22:40.
 */
public final class JacksonUtils {

    private JacksonUtils() {
    }

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtils.class);

    public static ObjectMapper getObjectMapper() {
        return JacksonJsonUtils.getObjectMapper();
    }

    /**
     * json 字符串 转 jsonNode
     *
     * @param json json 字符串
     * @return jsonNode
     */
    public static JsonNode toJsonNode(String json) {
        return JacksonJsonUtils.toJsonNode(json);
    }

    public static <T, I> JsonNode toJsonNode(T obj, Class<I> interfaceClass) {
        return JacksonJsonUtils.toJsonNode(obj, interfaceClass);
    }

    /**
     * bean、array、List、Map --&gt; json
     *
     * @param obj bean、array、List、Map
     * @return json string
     */
    public static String toJson(Object obj) {
        return JacksonJsonUtils.toJson(obj);
    }

    public static <T> T toBean(JsonNode jsonNode, Class<T> clazz) {
        return JacksonJsonUtils.toBean(jsonNode, clazz);
    }

    /**
     * json string --&gt; bean、Map、List(array)
     *
     * @param jsonStr string
     * @param clazz   class of bean
     * @param <T>     class
     * @return obj
     */
    public static <T> T jsonToBean(String jsonStr, Class<T> clazz) {
        return JacksonJsonUtils.jsonToBean(jsonStr, clazz);
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of bean
     * @param <T> class
     * @return obj
     */
    public static <T> T jsonStreamToBean(InputStream jsonInputStream, Class<T> clazz) {
        return JacksonJsonUtils.jsonStreamToBean(jsonInputStream, clazz);
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of list element
     * @param <T> class
     * @return obj
     */
    public static <T> List<T> jsonStreamToListBean(InputStream jsonInputStream, Class<T> clazz) {
        return JacksonJsonUtils.jsonStreamToListBean(jsonInputStream, clazz);
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of set element
     * @param <T> class
     * @return obj
     */
    public static <T> Set<T> jsonStreamToSetBean(InputStream jsonInputStream, Class<T> clazz) {
        return JacksonJsonUtils.jsonStreamToSetBean(jsonInputStream, clazz);
    }

    public static <T> T jsonToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        return JacksonJsonUtils.jsonToParametricBean(json, rawType, parameterClasses);
    }

    /**
     * bean、array、List、Map --&gt; json
     *
     * @param obj bean、array、List、Map
     * @return json string
     */
    public static String toJsonExcludeNullValue(Object obj) {
        return JacksonJsonUtils.toJsonExcludeNullValue(obj);
    }

    public static <T, I> T jsonToBean(String jsonStr, Class<T> clazz, Class<I> interfaceClass) {
        return JacksonJsonUtils.jsonToBean(jsonStr, clazz, interfaceClass);
    }

    public static <T, I> T jsonToBean(JsonNode jsonStr, Class<T> clazz, Class<I> interfaceClass) {
        return JacksonJsonUtils.jsonToBean(jsonStr, clazz, interfaceClass);
    }

    /**
     * @param jsonStr json格式的字符串
     * @param clazz   class of bean
     * @param <T>     class
     * @return T
     */
    public static <T> T jsonToBeanRefer(String jsonStr, Class<T> clazz) {
        return JacksonJsonUtils.jsonToBeanRefer(jsonStr, clazz);
    }

    /**
     * json to list
     *
     * @param jsonStr json格式的字符串
     * @param bean    class of bean
     * @param <T>     class
     * @return T
     */
    public static <T> List<T> jsonToListBean(String jsonStr, Class<T> bean) {
        return JacksonJsonUtils.jsonToListBean(jsonStr, bean);
    }

    public static <T> List<T> jsonToSetBean(String jsonStr, Class<T> bean) {
        return JacksonJsonUtils.jsonToSetBean(jsonStr, bean);
    }

    @SuppressWarnings("rawtypes")
    public static <T> Collection<T> jsonToCollectionBean(String jsonStr,
                                                   Class<? extends Collection> collectionType, Class<T> bean) {
        return JacksonJsonUtils.jsonToCollectionBean(jsonStr, collectionType, bean);
    }

    public static <T, I> List<T> jsonToListBean(String jsonStr, Class<T> bean, Class<I> interfaceClass) {
        return JacksonJsonUtils.jsonToListBean(jsonStr, bean, interfaceClass);
    }

    /**
     * xml to json
     * @param xml xml string
     * @return json string
     */
    public static String xmlToJson(String xml) {
        return JacksonXmlUtils.xmlToJson(xml);
    }

    /**
     * obj to xml
     * @param <T> T
     * @param object obj
     * @return xml string
     */
    public static <T> String toXml(T object) {
        return JacksonXmlUtils.toXml(object);
    }

    /**
     * xml to json
     * @param xml xml string
     * @return json string
     */
    public static JsonNode xmlToJsonNode(String xml) {
        return JacksonXmlUtils.xmlToJsonNode(xml);
    }

    public static <T> T xmlToBean(String xmlStr, Class<T> clazz) {
        return JacksonXmlUtils.xmlToBean(xmlStr, clazz);
    }

    public static <T> T xmlToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        return JacksonXmlUtils.xmlToParametricBean(json, rawType, parameterClasses);
    }

    public static <T> List<T> xmlToListBean(String xmlStr, Class<T> clazz) {
        return JacksonXmlUtils.xmlToListBean(xmlStr, clazz);
    }

    public static <T> Set<T> xmlToSetBean(String xmlStr, Class<T> clazz) {
        return JacksonXmlUtils.xmlToSetBean(xmlStr, clazz);
    }

    public static <T> Collection<T> xmlToCollectionBean(String xmlStr, Class<? extends Collection<?>> collectionClass, Class<T> clazz) {
        return JacksonXmlUtils.xmlToCollectionBean(xmlStr, collectionClass, clazz);
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz class of bean
     * @param <T> class
     * @return obj
     */
    public static <T> T xmlStreamToBean(InputStream xmlInputStream, Class<T> clazz) {
        return JacksonXmlUtils.xmlStreamToBean(xmlInputStream, clazz);
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz list element type
     * @param <T> class
     * @return obj
     */
    public static <T> List<T> xmlStreamToListBean(InputStream xmlInputStream, Class<T> clazz) {
        return JacksonXmlUtils.xmlStreamToListBean(xmlInputStream, clazz);
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz list element type
     * @param <T> class
     * @return obj
     */
    public static <T> Set<T> xmlStreamToSetBean(InputStream xmlInputStream, Class<T> clazz) {
        return JacksonXmlUtils.xmlStreamToSetBean(xmlInputStream, clazz);
    }

    /**
     * yaml to json
     * @param yaml yaml string
     * @return json string
     */
    public static String yamlToJson(String yaml) {
        return JacksonYamlUtils.yamlToJson(yaml);
    }

    /**
     * obj to yaml
     * @param <T> T
     * @param object obj
     * @return yaml string
     */
    public static <T> String toYaml(T object) {
        return JacksonYamlUtils.toYaml(object);
    }

    /**
     * yaml to json
     * @param yaml yaml string
     * @return json string
     */
    public static JsonNode yamlToJsonNode(String yaml) {
        return JacksonYamlUtils.yamlToJsonNode(yaml);
    }

    public static <T> T yamlToBean(String yaml, Class<T> clazz) {
        return JacksonYamlUtils.yamlToBean(yaml, clazz);
    }

    /**
     * @param yamlInputStream yaml stream
     * @param clazz class of bean
     * @param <T> class
     * @return obj
     */
    public static <T> T yamlStreamToBean(InputStream yamlInputStream, Class<T> clazz) {
        return JacksonYamlUtils.yamlStreamToBean(yamlInputStream, clazz);
    }

    public static <T> T yamlToCollectionBean(String xmlStr, Class<? extends Collection<?>> collectionClass,
                                          Class<T> clazz) {
        return JacksonYamlUtils.yamlToCollectionBean(xmlStr, collectionClass, clazz);
    }

    public static <T> T yamlToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        return JacksonYamlUtils.yamlToParametricBean(json, rawType, parameterClasses);
    }

    public static Map<String, String> yml2Properties(InputStream inputStream) {
        return JacksonYamlUtils.yml2Properties(inputStream);
    }
}