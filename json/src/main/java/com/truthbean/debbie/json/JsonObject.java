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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Represents a JSON object — an ordered map of name/value pairs.
 * <p>
 * Iteration order is preserved (insertion order via {@link LinkedHashMap}).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class JsonObject implements JsonElement, Iterable<Map.Entry<String, JsonElement>> {

    private final LinkedHashMap<String, JsonElement> members;

    /**
     * Creates an empty JsonObject.
     */
    public JsonObject() {
        this.members = new LinkedHashMap<>();
    }

    /**
     * Creates a JsonObject with the given initial capacity.
     * @param initialCapacity the initial capacity
     */
    public JsonObject(int initialCapacity) {
        this.members = new LinkedHashMap<>(initialCapacity);
    }

    // ============ add / set ============

    /**
     * Adds a key-value pair to this object.
     * @param property the key
     * @param value the value
     * @return this, for chaining
     */
    public JsonObject add(String property, JsonElement value) {
        members.put(Objects.requireNonNull(property), value == null ? JsonNull.INSTANCE : value);
        return this;
    }

    /**
     * Convenience method to add a string value.
     */
    public JsonObject addProperty(String property, String value) {
        return add(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    }

    /**
     * Convenience method to add a number value.
     */
    public JsonObject addProperty(String property, Number value) {
        return add(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    }

    /**
     * Convenience method to add a boolean value.
     */
    public JsonObject addProperty(String property, Boolean value) {
        return add(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    }

    /**
     * Convenience method to add a null value.
     */
    public JsonObject addNull(String property) {
        return add(property, JsonNull.INSTANCE);
    }

    // ============ get ============

    /**
     * @param property the key to look up
     * @return the value, or null if not present
     */
    public JsonElement get(String property) {
        return members.get(property);
    }

    /**
     * @param property the key to look up
     * @return the value as JsonObject, or null if not present or not an object
     */
    public JsonObject getAsJsonObject(String property) {
        JsonElement element = get(property);
        return element != null ? element.asJsonObject() : null;
    }

    /**
     * @param property the key to look up
     * @return the value as JsonArray, or null if not present or not an array
     */
    public JsonArray getAsJsonArray(String property) {
        JsonElement element = get(property);
        return element != null ? element.asJsonArray() : null;
    }

    /**
     * @param property the key to look up
     * @return the value as JsonPrimitive, or null if not present or not a primitive
     */
    public JsonPrimitive getAsJsonPrimitive(String property) {
        JsonElement element = get(property);
        return element != null ? element.asJsonPrimitive() : null;
    }

    /**
     * @param property the key to look up
     * @return the string value, or null if not present or not a string
     */
    public String getAsString(String property) {
        JsonPrimitive p = getAsJsonPrimitive(property);
        return p != null ? p.getAsString() : null;
    }

    /**
     * @param property the key to look up
     * @return the int value, or 0 if not present
     */
    public int getAsInt(String property) {
        JsonPrimitive p = getAsJsonPrimitive(property);
        return p != null ? p.getAsInt() : 0;
    }

    /**
     * @param property the key to look up
     * @return the long value, or 0L if not present
     */
    public long getAsLong(String property) {
        JsonPrimitive p = getAsJsonPrimitive(property);
        return p != null ? p.getAsLong() : 0L;
    }

    /**
     * @param property the key to look up
     * @return the double value, or 0.0 if not present
     */
    public double getAsDouble(String property) {
        JsonPrimitive p = getAsJsonPrimitive(property);
        return p != null ? p.getAsDouble() : 0.0;
    }

    /**
     * @param property the key to look up
     * @return the boolean value, or false if not present
     */
    public boolean getAsBoolean(String property) {
        JsonPrimitive p = getAsJsonPrimitive(property);
        return p != null && p.getAsBoolean();
    }

    /**
     * @param property the key to look up
     * @return the BigDecimal value, or null if not present
     */
    public BigDecimal getAsBigDecimal(String property) {
        JsonPrimitive p = getAsJsonPrimitive(property);
        return p != null ? p.getAsBigDecimal() : null;
    }

    /**
     * @param property the key to look up
     * @return the BigInteger value, or null if not present
     */
    public BigInteger getAsBigInteger(String property) {
        JsonPrimitive p = getAsJsonPrimitive(property);
        return p != null ? p.getAsBigInteger() : null;
    }

    // ============ query ============

    /**
     * @param property the key to check
     * @return true if this object contains the given key
     */
    public boolean has(String property) {
        return members.containsKey(property);
    }

    /**
     * @return the set of keys in this object
     */
    public Set<String> keySet() {
        return members.keySet();
    }

    /**
     * @return the number of entries in this object
     */
    public int size() {
        return members.size();
    }

    /**
     * @return true if this object is empty
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Removes the entry with the given key.
     * @param property the key to remove
     * @return the removed value, or null if not present
     */
    public JsonElement remove(String property) {
        return members.remove(property);
    }

    /**
     * Removes all entries.
     */
    public void clear() {
        members.clear();
    }

    @Override
    public boolean isJsonObject() {
        return true;
    }

    @Override
    public JsonObject asJsonObject() {
        return this;
    }

    @Override
    public Iterator<Map.Entry<String, JsonElement>> iterator() {
        return members.entrySet().iterator();
    }

    @Override
    public JsonElement deepCopy() {
        JsonObject result = new JsonObject(members.size());
        for (Map.Entry<String, JsonElement> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().deepCopy());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonObject that = (JsonObject) o;
        return members.equals(that.members);
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }

    @Override
    public String toString() {
        return members.toString();
    }
}