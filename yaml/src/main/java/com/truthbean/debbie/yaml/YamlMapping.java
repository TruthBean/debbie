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

import java.util.*;

/**
 * Represents a YAML mapping — an ordered map of name/value pairs.
 * <p>
 * Iteration order is preserved (insertion order via {@link LinkedHashMap}).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class YamlMapping implements YamlNode, Iterable<Map.Entry<String, YamlNode>> {

    private final LinkedHashMap<String, YamlNode> members;

    /**
     * Creates an empty YamlMapping.
     */
    public YamlMapping() {
        this.members = new LinkedHashMap<>();
    }

    /**
     * Creates a YamlMapping with the given initial capacity.
     * @param initialCapacity the initial capacity
     */
    public YamlMapping(int initialCapacity) {
        this.members = new LinkedHashMap<>(initialCapacity);
    }

    // ============ add / set ============

    /**
     * Adds a key-value pair to this mapping.
     * @param key the key
     * @param value the value
     * @return this, for chaining
     */
    public YamlMapping add(String key, YamlNode value) {
        members.put(Objects.requireNonNull(key), value == null ? YamlNull.INSTANCE : value);
        return this;
    }

    /**
     * Convenience method to add a string value.
     */
    public YamlMapping add(String key, String value) {
        return add(key, value == null ? YamlNull.INSTANCE : new YamlScalar(value));
    }

    /**
     * Convenience method to add a number value.
     */
    public YamlMapping add(String key, Number value) {
        return add(key, value == null ? YamlNull.INSTANCE : new YamlScalar(value));
    }

    /**
     * Convenience method to add a boolean value.
     */
    public YamlMapping add(String key, Boolean value) {
        return add(key, value == null ? YamlNull.INSTANCE : new YamlScalar(value));
    }

    /**
     * Convenience method to add a null value.
     */
    public YamlMapping addNull(String key) {
        return add(key, YamlNull.INSTANCE);
    }

    // ============ get ============

    /**
     * @param key the key to look up
     * @return the value, or null if not present
     */
    public YamlNode get(String key) {
        return members.get(key);
    }

    /**
     * @param key the key to look up
     * @return the value as YamlMapping, or null if not present or not a mapping
     */
    public YamlMapping getAsMapping(String key) {
        YamlNode node = get(key);
        return node != null ? node.asMapping() : null;
    }

    /**
     * @param key the key to look up
     * @return the value as YamlSequence, or null if not present or not a sequence
     */
    public YamlSequence getAsSequence(String key) {
        YamlNode node = get(key);
        return node != null ? node.asSequence() : null;
    }

    /**
     * @param key the key to look up
     * @return the value as YamlScalar, or null if not present or not a scalar
     */
    public YamlScalar getAsScalar(String key) {
        YamlNode node = get(key);
        return node != null ? node.asScalar() : null;
    }

    /**
     * @param key the key to look up
     * @return the string value, or null if not present or not a string
     */
    public String getAsString(String key) {
        YamlScalar s = getAsScalar(key);
        return s != null ? s.getAsString() : null;
    }

    /**
     * @param key the key to look up
     * @return the int value, or 0 if not present
     */
    public int getAsInt(String key) {
        YamlScalar s = getAsScalar(key);
        return s != null ? s.getAsInt() : 0;
    }

    /**
     * @param key the key to look up
     * @return the long value, or 0L if not present
     */
    public long getAsLong(String key) {
        YamlScalar s = getAsScalar(key);
        return s != null ? s.getAsLong() : 0L;
    }

    /**
     * @param key the key to look up
     * @return the double value, or 0.0 if not present
     */
    public double getAsDouble(String key) {
        YamlScalar s = getAsScalar(key);
        return s != null ? s.getAsDouble() : 0.0;
    }

    /**
     * @param key the key to look up
     * @return the boolean value, or false if not present
     */
    public boolean getAsBoolean(String key) {
        YamlScalar s = getAsScalar(key);
        return s != null && s.getAsBoolean();
    }

    // ============ query ============

    /**
     * @param key the key to check
     * @return true if this mapping contains the given key
     */
    public boolean has(String key) {
        return members.containsKey(key);
    }

    /**
     * @return the set of keys in this mapping
     */
    public Set<String> keySet() {
        return members.keySet();
    }

    /**
     * @return the number of entries in this mapping
     */
    public int size() {
        return members.size();
    }

    /**
     * @return true if this mapping is empty
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Removes the entry with the given key.
     * @param key the key to remove
     * @return the removed value, or null if not present
     */
    public YamlNode remove(String key) {
        return members.remove(key);
    }

    /**
     * Removes all entries.
     */
    public void clear() {
        members.clear();
    }

    @Override
    public boolean isMapping() {
        return true;
    }

    @Override
    public YamlMapping asMapping() {
        return this;
    }

    @Override
    public Iterator<Map.Entry<String, YamlNode>> iterator() {
        return members.entrySet().iterator();
    }

    @Override
    public YamlNode deepCopy() {
        YamlMapping result = new YamlMapping(members.size());
        for (Map.Entry<String, YamlNode> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().deepCopy());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YamlMapping that = (YamlMapping) o;
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