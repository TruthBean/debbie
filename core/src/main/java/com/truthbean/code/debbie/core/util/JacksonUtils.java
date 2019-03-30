package com.truthbean.code.debbie.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created by TruthBean on 2017-08-02 22:40.
 */
public final class JacksonUtils {

    private JacksonUtils() {
    }

    /**
     * djcps logger
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

    }

    /**
     * ObjectMapper 实例
     */
    private static final ObjectMapper OBJECT_MAPPER = Instance.OBJECT_MAPPER;
    private static final XmlMapper XML_MAPPER = Instance.XML_MAPPER;

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

    public static <T> T xmlToBean(String xmlStr, Class<T> clazz) {
        var mapper = XML_MAPPER.copy();
        try {
            return mapper.readValue(xmlStr, clazz);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
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
            obj = XML_MAPPER.readValue(xmlInputStream, clazz);
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
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
        JsonNode node;
        var json = "{}";
        try {
            node = xmlMapper.readTree(xml.getBytes());
            json = toJson(node);
        } catch (IOException e) {
            e.printStackTrace();
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
        String xml = "<xml></xml>";
        try {
            xml = xmlMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return node;
    }
}