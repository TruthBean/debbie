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
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.serialize.TextSerializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.1
 * Created on 2020-10-30 14:34
 */
public class JacksonYamlUtils implements TextSerializable {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonYamlUtils.class);

    private static final class Instance {

        static final YAMLMapper YAML_MAPPER = new YAMLMapper();

        static {
            YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            YAML_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            YAML_MAPPER.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
            YAML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

    }

    /**
     * ObjectMapper 实例
     */
    private static final YAMLMapper YAML_MAPPER = Instance.YAML_MAPPER;

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
            json = JacksonJsonUtils.toJson(node);
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

    @Override
    public String serialize(Object obj) {
        return toYaml(obj);
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

    @Override
    public <T> T deserialize(String text, Class<T> type) {
        return yamlToBean(text, type);
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

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) {
        return yamlStreamToBean(inputStream, type);
    }

    public static <T> T yamlToCollectionBean(String xmlStr, Class<? extends Collection<?>> collectionClass,
                                             Class<T> clazz) {
        var yamlMapper = YAML_MAPPER.copy();
        T obj = null;
        try {
            obj = yamlMapper.readValue(xmlStr, YAML_MAPPER.getTypeFactory()
                    .constructCollectionType(collectionClass, clazz));
        } catch (IOException e) {
            LOGGER.error("xml inputStream to bean error", e);
        }
        return obj;
    }

    public static <T> T yamlToParametricBean(String json, Class<T> rawType, Class<?>... parameterClasses) {
        var mapper = YAML_MAPPER.copy();
        try {
            return mapper.readValue(json, YAML_MAPPER.getTypeFactory()
                    .constructParametricType(rawType, parameterClasses));
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static Map<String, String> yml2Properties(InputStream inputStream) {
        final String DOT = ".";
        Map<String, String> map = new HashMap<>();
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLParser parser = yamlFactory.createParser(new InputStreamReader(inputStream, Charset.defaultCharset()));

            StringBuilder key = new StringBuilder();
            String value;
            JsonToken token = parser.nextToken();
            while (token != null) {
                if (!JsonToken.START_OBJECT.equals(token)) {
                    if (JsonToken.FIELD_NAME.equals(token)) {
                        if (key.length() > 0) {
                            key.append(DOT);
                        }
                        key.append(parser.getCurrentName());

                        token = parser.nextToken();
                        if (JsonToken.START_OBJECT.equals(token)) {
                            continue;
                        }
                        value = parser.getText();
                        map.put(key.toString(), value);

                        int dotOffset = key.lastIndexOf(DOT);
                        if (dotOffset > 0) {
                            key = new StringBuilder(key.substring(0, dotOffset));
                        }
                        value = null;
                    } else if (JsonToken.END_OBJECT.equals(token)) {
                        int dotOffset = key.lastIndexOf(DOT);
                        if (dotOffset > 0) {
                            key = new StringBuilder(key.substring(0, dotOffset));
                        } else {
                            key = new StringBuilder();
                        }
                    }
                }

                token = parser.nextToken();
            }
            parser.close();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return map;
    }
}
