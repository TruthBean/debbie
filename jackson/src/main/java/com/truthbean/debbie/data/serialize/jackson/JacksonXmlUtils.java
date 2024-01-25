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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.serialize.TextSerializable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-30 14:29
 */
public class JacksonXmlUtils implements TextSerializable {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonXmlUtils.class);

    private static final class Instance {
        static final XmlMapper XML_MAPPER = new XmlMapper();

        static {
            XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            XML_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            XML_MAPPER.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
            XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

    }

    /**
     * ObjectMapper 实例
     */
    private static final XmlMapper XML_MAPPER = Instance.XML_MAPPER;


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
            json = JacksonJsonUtils.toJson(node);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return json;
    }

    @Override
    public String serialize(Object obj) {
        return toXml(obj);
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

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) {
        return xmlStreamToBean(inputStream, type);
    }

    @Override
    public <T> T deserialize(String text, Class<T> type) {
        return xmlToBean(text, type);
    }

    public static <T> T xmlToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        var mapper = XML_MAPPER.copy();
        try {
            return mapper.readValue(json, XML_MAPPER.getTypeFactory()
                    .constructParametricType(rawType, parameterClasses));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static <T> List<T> xmlToListBean(String xmlStr, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        List<T> obj = null;
        try {
            obj = xmlMapper.readValue(xmlStr, XML_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> Set<T> xmlToSetBean(String xmlStr, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        Set<T> obj = null;
        try {
            obj = xmlMapper.readValue(xmlStr, XML_MAPPER.getTypeFactory()
                    .constructCollectionType(Set.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> Collection<T> xmlToCollectionBean(String xmlStr, Class<? extends Collection<?>> collectionClass, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        Collection<T> obj = null;
        try {
            obj = xmlMapper.readValue(xmlStr, XML_MAPPER.getTypeFactory()
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
            LOGGER.error("xml inputStream to bean error\n", e);
        }
        return obj;
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz list element type
     * @param <T> class
     * @return obj
     */
    public static <T> List<T> xmlStreamToListBean(InputStream xmlInputStream, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        List<T> obj = null;
        try {
            obj = xmlMapper.readValue(xmlInputStream, XML_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error\n", e);
        }
        return obj;
    }

    /**
     * @param xmlInputStream xml stream
     * @param clazz list element type
     * @param <T> class
     * @return obj
     */
    public static <T> Set<T> xmlStreamToSetBean(InputStream xmlInputStream, Class<T> clazz) {
        var xmlMapper = XML_MAPPER.copy();
        Set<T> obj = null;
        try {
            obj = xmlMapper.readValue(xmlInputStream, XML_MAPPER.getTypeFactory()
                    .constructCollectionType(Set.class, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error\n", e);
        }
        return obj;
    }

}
