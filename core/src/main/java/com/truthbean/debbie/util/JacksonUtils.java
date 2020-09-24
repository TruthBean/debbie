/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created by TruthBean on 2017-08-02 22:40.
 */
public final class JacksonUtils {

    private JacksonUtils() {
    }

    /**
     * slf4j logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtils.class);

    private static final class Instance {
        /**
         * ObjectMapper 实例
         */
        static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static {
            OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OBJECT_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            OBJECT_MAPPER.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        }

        static final XmlMapper XML_MAPPER = new XmlMapper();

        static {
            XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            XML_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            XML_MAPPER.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        }

        static final YAMLMapper YAML_MAPPER = new YAMLMapper();

        static {
            YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            YAML_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            YAML_MAPPER.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        }

    }

    /**
     * ObjectMapper 实例
     */
    private static final ObjectMapper OBJECT_MAPPER = Instance.OBJECT_MAPPER;
    private static final XmlMapper XML_MAPPER = Instance.XML_MAPPER;
    private static final YAMLMapper YAML_MAPPER = Instance.YAML_MAPPER;

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * json 字符串 转 jsonNode
     *
     * @param json json 字符串
     * @return jsonNode
     */
    public static JsonNode toJsonNode(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return NullNode.getInstance();
    }

    public static <T, I> JsonNode toJsonNode(T obj, Class<I> interfaceClass) {
        try {
            var mapper = OBJECT_MAPPER.copy();
            mapper.addMixIn(obj.getClass(), interfaceClass);
            return mapper.valueToTree(obj);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return NullNode.getInstance();
    }

    /**
     * bean、array、List、Map --&gt; json
     *
     * @param obj bean、array、List、Map
     * @return json string
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T> T toBean(JsonNode jsonNode, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
        }
        return null;
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
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of bean
     * @param <T> class
     * @return obj
     */
    public static <T> T jsonStreamToBean(InputStream jsonInputStream, Class<T> clazz) {
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(jsonInputStream, clazz);
        } catch (IOException e) {
            LOGGER.error("json inputStream to bean error", e);
        }
        return obj;
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of list element
     * @param <T> class
     * @return obj
     */
    public static <T> T jsonStreamToListBean(InputStream jsonInputStream, Class<T> clazz) {
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(jsonInputStream, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            LOGGER.error("json inputStream to bean error", e);
        }
        return obj;
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of set element
     * @param <T> class
     * @return obj
     */
    public static <T> T jsonStreamToSetBean(InputStream jsonInputStream, Class<T> clazz) {
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(jsonInputStream, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(Set.class, clazz));
        } catch (IOException e) {
            LOGGER.error("json inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> T jsonToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        T obj = null;
        try {
            obj = JacksonUtils.OBJECT_MAPPER.readValue(json, JacksonUtils.OBJECT_MAPPER.getTypeFactory()
                    .constructParametricType(rawType, parameterClasses));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return obj;
    }

    /**
     * bean、array、List、Map --&gt; json
     *
     * @param obj bean、array、List、Map
     * @return json string
     */
    public static String toJsonExcludeNullValue(Object obj) {
        var mapper = OBJECT_MAPPER.copy();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T, I> T jsonToBean(String jsonStr, Class<T> clazz, Class<I> interfaceClass) {
        try {
            var mapper = OBJECT_MAPPER.copy();
            mapper.addMixIn(clazz, interfaceClass);
            return mapper.readValue(jsonStr, clazz);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T, I> T jsonToBean(JsonNode jsonStr, Class<T> clazz, Class<I> interfaceClass) {
        try {
            var mapper = OBJECT_MAPPER.copy();
            mapper.addMixIn(clazz, interfaceClass);
            return mapper.treeToValue(jsonStr, clazz);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * @param jsonStr json格式的字符串
     * @param clazz   class of bean
     * @param <T>     class
     * @return T
     */
    public static <T> T jsonToBeanRefer(String jsonStr, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<T>() {
            });
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
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
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, bean));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T> List<T> jsonToSetBean(String jsonStr, Class<T> bean) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(Set.class, bean));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static <T> List<T> jsonToCollectionBean(String jsonStr,
                                                   Class<? extends Collection> collectionType, Class<T> bean) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(collectionType, bean));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T, I> List<T> jsonToListBean(String jsonStr, Class<T> bean, Class<I> interfaceClass) {
        try {
            var mapper = OBJECT_MAPPER.copy();
            mapper.addMixIn(bean, interfaceClass);
            return mapper.readValue(jsonStr, mapper.getTypeFactory()
                    .constructCollectionType(List.class, bean));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * xml to json
     * @param xml xml string
     * @return json string
     */
    public static String xmlToJson(String xml) {
        var xmlMapper = XML_MAPPER.copy();
        var json = "{}";
        try {
            var node = xmlMapper.readTree(xml.getBytes());
            json = toJson(node);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return json;
    }

    /**
     * obj to xml
     * @param <T> T
     * @param object obj
     * @return xml string
     */
    public static <T> String toXml(T object) {
        var xmlMapper = XML_MAPPER.copy();
        var xml = "<xml></xml>";
        try {
            xml = xmlMapper.writeValueAsString(object);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return xml;
    }

    /**
     * xml to json
     * @param xml xml string
     * @return json string
     */
    public static JsonNode xmlToJsonNode(String xml) {
        var xmlMapper = XML_MAPPER.copy();
        JsonNode node = NullNode.getInstance();
        try {
            node = xmlMapper.readTree(xml.getBytes());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return node;
    }

    public static <T> T xmlToBean(String xmlStr, Class<T> clazz) {
        var mapper = XML_MAPPER.copy();
        try {
            return mapper.readValue(xmlStr, clazz);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T> T xmlToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        var mapper = XML_MAPPER.copy();
        try {
            return mapper.readValue(json, JacksonUtils.OBJECT_MAPPER.getTypeFactory()
                    .constructParametricType(rawType, parameterClasses));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T> T xmlToListBean(String xmlStr, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        T obj = null;
        try {
            obj = xmlMapper.readValue(xmlStr, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> T xmlToSetBean(String xmlStr, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        T obj = null;
        try {
            obj = xmlMapper.readValue(xmlStr, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(Set.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> T xmlToCollectionBean(String xmlStr, Class<? extends Collection<?>> collectionClass, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        T obj = null;
        try {
            obj = xmlMapper.readValue(xmlStr, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(collectionClass, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz class of bean
     * @param <T> class
     * @return obj
     */
    public static <T> T xmlStreamToBean(InputStream xmlInputStream, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        T obj = null;
        try {
            obj = xmlMapper.readValue(xmlInputStream, clazz);
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz list element type
     * @param <T> class
     * @return obj
     */
    public static <T> T xmlStreamToListBean(InputStream xmlInputStream, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        T obj = null;
        try {
            obj = xmlMapper.readValue(xmlInputStream, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz list element type
     * @param <T> class
     * @return obj
     */
    public static <T> T xmlStreamToSetBean(InputStream xmlInputStream, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        T obj = null;
        try {
            obj = xmlMapper.readValue(xmlInputStream, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(Set.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    /**
     * yaml to json
     * @param yaml yaml string
     * @return json string
     */
    public static String yamlToJson(String yaml) {
        var yamlMapper = YAML_MAPPER.copy();
        var json = "{}";
        try {
            var node = yamlMapper.readTree(yaml.getBytes());
            json = toJson(node);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return json;
    }

    /**
     * obj to yaml
     * @param <T> T
     * @param object obj
     * @return yaml string
     */
    public static <T> String toYaml(T object) {
        var yamlMapper = YAML_MAPPER.copy();
        var yaml = "";
        try {
            yaml = yamlMapper.writeValueAsString(object);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return yaml;
    }

    /**
     * yaml to json
     * @param yaml yaml string
     * @return json string
     */
    public static JsonNode yamlToJsonNode(String yaml) {
        var yamlMapper = YAML_MAPPER.copy();
        JsonNode node = NullNode.getInstance();
        try {
            node = yamlMapper.readTree(yaml.getBytes());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return node;
    }

    public static <T> T yamlToBean(String yaml, Class<T> clazz) {
        var mapper = YAML_MAPPER.copy();
        try {
            return mapper.readValue(yaml, clazz);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * @param yamlInputStream yaml stream
     * @param clazz class of bean
     * @param <T> class
     * @return obj
     */
    public static <T> T yamlStreamToBean(InputStream yamlInputStream, Class<T> clazz) {
        var yamlMapper = YAML_MAPPER.copy();
        T obj = null;
        try {
            obj = yamlMapper.readValue(yamlInputStream, clazz);
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> T yamlToCollectionBean(String xmlStr, Class<? extends Collection<?>> collectionClass,
                                          Class<T> clazz) {
        var yamlMapper = YAML_MAPPER.copy();
        T obj = null;
        try {
            obj = yamlMapper.readValue(xmlStr, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(collectionClass, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> T yamlToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        var mapper = YAML_MAPPER.copy();
        try {
            return mapper.readValue(json, JacksonUtils.OBJECT_MAPPER.getTypeFactory()
                    .constructParametricType(rawType, parameterClasses));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }
}