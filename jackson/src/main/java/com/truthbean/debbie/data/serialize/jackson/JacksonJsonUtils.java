/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.serialize.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.NullNode;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.serialize.TextSerializable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.1
 * Created on 2020-10-30 14:24
 */
public class JacksonJsonUtils implements TextSerializable {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonJsonUtils.class);

    private static final class Instance {
        /**
         * ObjectMapper 实例
         */
        static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static {
            OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OBJECT_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            OBJECT_MAPPER.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

            OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        }
    }

    /**
     * ObjectMapper 实例
     */
    private static final ObjectMapper OBJECT_MAPPER = Instance.OBJECT_MAPPER;

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

    @Override
    public String serialize(Object obj) {
        return toJson(obj);
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

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        return jsonToBean(json, type);
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
            LOGGER.error("json inputStream to bean error\n", e);
        }
        return obj;
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) {
        return jsonStreamToBean(inputStream, type);
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of list element
     * @param <T> class
     * @return obj
     */
    public static <T> List<T> jsonStreamToListBean(InputStream jsonInputStream, Class<T> clazz) {
        List<T> obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(jsonInputStream, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            LOGGER.error("json inputStream to bean error\n", e);
        }
        return obj;
    }

    /**
     * @param jsonInputStream json stream
     * @param clazz class of set element
     * @param <T> class
     * @return obj
     */
    public static <T> Set<T> jsonStreamToSetBean(InputStream jsonInputStream, Class<T> clazz) {
        Set<T> obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(jsonInputStream, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(Set.class, clazz));
        } catch (IOException e) {
            LOGGER.error("json inputStream to bean error\n", e);
        }
        return obj;
    }

    public static <T> T jsonToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory()
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
            return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<>() {
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
    public static <T> Collection<T> jsonToCollectionBean(String jsonStr,
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
}
