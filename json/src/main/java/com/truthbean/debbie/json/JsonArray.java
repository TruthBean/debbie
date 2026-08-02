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
import java.util.*;

/**
 * Represents a JSON array — an ordered list of {@link JsonElement}s.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class JsonArray implements JsonElement, Iterable<JsonElement> {

    private final List<JsonElement> elements;

    /**
     * Creates an empty JsonArray.
     */
    public JsonArray() {
        this.elements = new ArrayList<>();
    }

    /**
     * Creates a JsonArray with the given initial capacity.
     * @param initialCapacity the initial capacity
     */
    public JsonArray(int initialCapacity) {
        this.elements = new ArrayList<>(initialCapacity);
    }

    // ============ add ============

    /**
     * Adds a JSON element to the end of this array.
     * @param element the element to add
     * @return this, for chaining
     */
    public JsonArray add(JsonElement element) {
        elements.add(element == null ? JsonNull.INSTANCE : element);
        return this;
    }

    /**
     * Convenience method to add a string value.
     */
    public JsonArray add(String value) {
        return add(value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    }

    /**
     * Convenience method to add a number value.
     */
    public JsonArray add(Number value) {
        return add(value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    }

    /**
     * Convenience method to add a boolean value.
     */
    public JsonArray add(Boolean value) {
        return add(value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    }

    /**
     * Convenience method to add a null value.
     */
    public JsonArray addNull() {
        elements.add(JsonNull.INSTANCE);
        return this;
    }

    // ============ get ============

    /**
     * @param index the index
     * @return the element at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public JsonElement get(int index) {
        return elements.get(index);
    }

    /**
     * @param index the index
     * @return the element as a JsonObject
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public JsonObject getAsJsonObject(int index) {
        return elements.get(index).asJsonObject();
    }

    /**
     * @param index the index
     * @return the element as a JsonArray
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public JsonArray getAsJsonArray(int index) {
        return elements.get(index).asJsonArray();
    }

    /**
     * @param index the index
     * @return the element as a JsonPrimitive
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public JsonPrimitive getAsJsonPrimitive(int index) {
        return elements.get(index).asJsonPrimitive();
    }

    /**
     * @param index the index
     * @return the string value at the given index
     */
    public String getAsString(int index) {
        return getAsJsonPrimitive(index).getAsString();
    }

    /**
     * @param index the index
     * @return the int value at the given index
     */
    public int getAsInt(int index) {
        return getAsJsonPrimitive(index).getAsInt();
    }

    /**
     * @param index the index
     * @return the long value at the given index
     */
    public long getAsLong(int index) {
        return getAsJsonPrimitive(index).getAsLong();
    }

    /**
     * @param index the index
     * @return the double value at the given index
     */
    public double getAsDouble(int index) {
        return getAsJsonPrimitive(index).getAsDouble();
    }

    /**
     * @param index the index
     * @return the boolean value at the given index
     */
    public boolean getAsBoolean(int index) {
        return getAsJsonPrimitive(index).getAsBoolean();
    }

    /**
     * @param index the index
     * @return the BigDecimal value at the given index
     */
    public BigDecimal getAsBigDecimal(int index) {
        return getAsJsonPrimitive(index).getAsBigDecimal();
    }

    // ============ query ============

    /**
     * @return the number of elements in this array
     */
    public int size() {
        return elements.size();
    }

    /**
     * @return true if this array is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Replaces the element at the given index.
     * @param index the index
     * @param element the new element
     * @return the previous element
     */
    public JsonElement set(int index, JsonElement element) {
        return elements.set(index, element == null ? JsonNull.INSTANCE : element);
    }

    /**
     * Removes the element at the given index.
     * @param index the index
     * @return the removed element
     */
    public JsonElement remove(int index) {
        return elements.remove(index);
    }

    /**
     * Removes all elements.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * @return a copy of the backing list
     */
    public List<JsonElement> asList() {
        return new ArrayList<>(elements);
    }

    @Override
    public boolean isJsonArray() {
        return true;
    }

    @Override
    public JsonArray asJsonArray() {
        return this;
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return elements.iterator();
    }

    @Override
    public JsonElement deepCopy() {
        JsonArray result = new JsonArray(elements.size());
        for (JsonElement element : elements) {
            result.add(element.deepCopy());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonArray jsonArray = (JsonArray) o;
        return elements.equals(jsonArray.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}