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

/**
 * Base interface for all JSON element types.
 * <p>
 * Represents a node in a JSON document tree. Each element can be
 * one of: {@link JsonObject}, {@link JsonArray}, {@link JsonPrimitive}, or {@link JsonNull}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface JsonElement {

    /**
     * @return true if this element is a JSON object
     */
    default boolean isJsonObject() {
        return false;
    }

    /**
     * @return true if this element is a JSON array
     */
    default boolean isJsonArray() {
        return false;
    }

    /**
     * @return true if this element is a JSON primitive (string, number, boolean)
     */
    default boolean isJsonPrimitive() {
        return false;
    }

    /**
     * @return true if this element is JSON null
     */
    default boolean isJsonNull() {
        return false;
    }

    /**
     * @return this cast to JsonObject, or null if not a JsonObject
     */
    default JsonObject asJsonObject() {
        return null;
    }

    /**
     * @return this cast to JsonArray, or null if not a JsonArray
     */
    default JsonArray asJsonArray() {
        return null;
    }

    /**
     * @return this cast to JsonPrimitive, or null if not a JsonPrimitive
     */
    default JsonPrimitive asJsonPrimitive() {
        return null;
    }

    /**
     * @return a deep copy of this element
     */
    JsonElement deepCopy();

    /**
     * @return the JSON string representation
     */
    @Override
    String toString();
}