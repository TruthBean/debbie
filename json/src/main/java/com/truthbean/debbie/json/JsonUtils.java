/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.json;

import com.truthbean.debbie.data.serialize.TextSerializable;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Convenience utility class for JSON serialization and deserialization.
 * <p>
 * Provides static methods for common JSON operations and implements
 * {@link TextSerializable} for integration with the Debbie framework.
 * <p>
 * All methods are pure Java with no third-party dependencies.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class JsonUtils implements TextSerializable {

    private static final JsonUtils INSTANCE = new JsonUtils();

    // ============ parse (JSON string -> JsonElement) ============

    /**
     * Parses a JSON string into a {@link JsonElement} tree.
     *
     * @param json the JSON string
     * @return the parsed element
     * @throws JsonException if the JSON is invalid
     */
    public static JsonElement parse(String json) {
        return JsonParser.parse(json);
    }

    /**
     * Parses a JSON string from an {@link InputStream}.
     *
     * @param inputStream the input stream
     * @return the parsed element
     * @throws JsonException if the JSON is invalid or I/O error occurs
     */
    public static JsonElement parse(InputStream inputStream) {
        try {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parse(json);
        } catch (IOException e) {
            throw new JsonException("Failed to read JSON from input stream", e);
        }
    }

    // ============ serialize (JsonElement -> JSON string) ============

    /**
     * Serializes a {@link JsonElement} to a compact JSON string.
     *
     * @param element the element to serialize
     * @return the JSON string
     */
    public static String serialize(JsonElement element) {
        return new JsonSerializer(false).serialize(element);
    }

    /**
     * Serializes a {@link JsonElement} to a pretty-printed JSON string.
     *
     * @param element the element to serialize
     * @return the formatted JSON string
     */
    public static String prettyPrint(JsonElement element) {
        return new JsonSerializer(true).serialize(element);
    }

    // ============ Java object -> JSON string ============

    /**
     * Converts a Java object to a compact JSON string.
     *
     * @param obj the Java object
     * @return the JSON string
     */
    public static String toJson(Object obj) {
        JsonElement element = JsonMapper.toJsonElement(obj);
        return new JsonSerializer(false).serialize(element);
    }

    /**
     * Converts a Java object to a pretty-printed JSON string.
     *
     * @param obj the Java object
     * @return the formatted JSON string
     */
    public static String toJsonPretty(Object obj) {
        JsonElement element = JsonMapper.toJsonElement(obj);
        return new JsonSerializer(true).serialize(element);
    }

    // ============ JSON string -> Java object ============

    /**
     * Parses a JSON string and converts it to a Java object of the specified type.
     *
     * @param <T>   the target type
     * @param json  the JSON string
     * @param clazz the target class
     * @return the converted Java object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        JsonElement element = JsonParser.parse(json);
        return JsonMapper.fromJsonElement(element, clazz);
    }

    /**
     * Parses a JSON string and converts it to a Java object with generic type parameters.
     *
     * @param <T>           the raw target type
     * @param json          the JSON string
     * @param rawType       the raw class
     * @param typeArguments the type arguments
     * @return the converted Java object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<T> rawType, Class<?>... typeArguments) {
        JsonElement element = JsonParser.parse(json);
        return (T) JsonMapper.fromJsonElement(element, rawType, typeArguments);
    }

    /**
     * Parses a JSON string from an input stream and converts it to a Java object.
     *
     * @param <T>   the target type
     * @param inputStream the input stream
     * @param clazz the target class
     * @return the converted Java object
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        JsonElement element = parse(inputStream);
        return JsonMapper.fromJsonElement(element, clazz);
    }

    // ============ TextSerializable implementation ============

    @Override
    public String serialize(Object obj) {
        return toJson(obj);
    }

    @Override
    public <T> T deserialize(String text, Class<T> type) {
        return fromJson(text, type);
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) {
        return fromJson(inputStream, type);
    }

    /**
     * @return the singleton instance
     */
    public static JsonUtils getInstance() {
        return INSTANCE;
    }
}