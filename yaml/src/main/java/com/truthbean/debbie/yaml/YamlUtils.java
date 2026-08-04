/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.yaml;

import com.truthbean.debbie.data.serialize.TextSerializable;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Convenience utility class for YAML serialization and deserialization.
 * <p>
 * Provides static methods for common YAML operations and implements
 * {@link TextSerializable} for integration with the Debbie framework.
 * <p>
 * All methods are pure Java with no third-party dependencies.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class YamlUtils implements TextSerializable {

    private static final YamlUtils INSTANCE = new YamlUtils();

    // ============ parse (YAML string -> YamlDocument) ============

    /**
     * Parses a YAML string into a {@link YamlDocument}.
     *
     * @param yaml the YAML string
     * @return the parsed document
     * @throws YamlException if the YAML is invalid
     */
    public static YamlDocument parse(String yaml) {
        return YamlParser.parse(yaml);
    }

    /**
     * Parses a YAML string from an {@link InputStream}.
     *
     * @param inputStream the input stream
     * @return the parsed document
     * @throws YamlException if the YAML is invalid or I/O error occurs
     */
    public static YamlDocument parse(InputStream inputStream) {
        try {
            String yaml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return YamlParser.parse(yaml);
        } catch (IOException e) {
            throw new YamlException("Failed to read YAML from input stream", e);
        }
    }

    // ============ serialize (YamlDocument/Node -> YAML string) ============

    /**
     * Serializes a {@link YamlDocument} to a YAML string.
     *
     * @param document the document to serialize
     * @return the YAML string
     */
    public static String serialize(YamlDocument document) {
        return new YamlSerializer(false).serialize(document);
    }

    /**
     * Serializes a {@link YamlNode} to a YAML string.
     *
     * @param node the node to serialize
     * @return the YAML string
     */
    public static String serialize(YamlNode node) {
        return new YamlSerializer(false).serialize(node);
    }

    /**
     * Serializes a {@link YamlDocument} to a pretty-printed YAML string.
     *
     * @param document the document to serialize
     * @return the formatted YAML string
     */
    public static String prettyPrint(YamlDocument document) {
        return new YamlSerializer(true).serialize(document);
    }

    /**
     * Serializes a {@link YamlNode} to a pretty-printed YAML string.
     *
     * @param node the node to serialize
     * @return the formatted YAML string
     */
    public static String prettyPrint(YamlNode node) {
        return new YamlSerializer(true).serialize(node);
    }

    // ============ Java object -> YAML string ============

    /**
     * Converts a Java object to a YAML string.
     *
     * @param obj the Java object
     * @return the YAML string
     */
    public static String toYaml(Object obj) {
        YamlNode node = YamlMapper.toYamlNode(obj);
        return new YamlSerializer(false).serialize(node);
    }

    /**
     * Converts a Java object to a pretty-printed YAML string.
     *
     * @param obj the Java object
     * @return the formatted YAML string
     */
    public static String toYamlPretty(Object obj) {
        YamlNode node = YamlMapper.toYamlNode(obj);
        return new YamlSerializer(true).serialize(node);
    }

    // ============ YAML string -> Java object ============

    /**
     * Parses a YAML string and converts it to a Java object of the specified type.
     *
     * @param <T>   the target type
     * @param yaml  the YAML string
     * @param clazz the target class
     * @return the converted Java object
     */
    public static <T> T fromYaml(String yaml, Class<T> clazz) {
        YamlDocument document = YamlParser.parse(yaml);
        return YamlMapper.fromYamlNode(document.getRoot(), clazz);
    }

    /**
     * Parses a YAML string and converts it to a Java object with generic type parameters.
     *
     * @param <T>           the raw target type
     * @param yaml          the YAML string
     * @param rawType       the raw class
     * @param typeArguments the type arguments
     * @return the converted Java object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromYaml(String yaml, Class<T> rawType, Class<?>... typeArguments) {
        YamlDocument document = YamlParser.parse(yaml);
        return (T) YamlMapper.fromYamlNode(document.getRoot(), rawType, typeArguments);
    }

    /**
     * Parses a YAML string from an input stream and converts it to a Java object.
     *
     * @param <T>   the target type
     * @param inputStream the input stream
     * @param clazz the target class
     * @return the converted Java object
     */
    public static <T> T fromYaml(InputStream inputStream, Class<T> clazz) {
        YamlDocument document = parse(inputStream);
        return YamlMapper.fromYamlNode(document.getRoot(), clazz);
    }

    // ============ TextSerializable implementation ============

    @Override
    public String serialize(Object obj) {
        return toYaml(obj);
    }

    @Override
    public <T> T deserialize(String text, Class<T> type) {
        return fromYaml(text, type);
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) {
        return fromYaml(inputStream, type);
    }

    /**
     * @return the singleton instance
     */
    public static YamlUtils getInstance() {
        return INSTANCE;
    }
}