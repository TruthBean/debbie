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
 * Represents a YAML sequence (ordered list of {@link YamlNode}s).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class YamlSequence implements YamlNode, Iterable<YamlNode> {

    private final List<YamlNode> elements;

    /**
     * Creates an empty YamlSequence.
     */
    public YamlSequence() {
        this.elements = new ArrayList<>();
    }

    /**
     * Creates a YamlSequence with the given initial capacity.
     * @param initialCapacity the initial capacity
     */
    public YamlSequence(int initialCapacity) {
        this.elements = new ArrayList<>(initialCapacity);
    }

    // ============ add ============

    /**
     * Adds a YAML node to the end of this sequence.
     * @param node the node to add
     * @return this, for chaining
     */
    public YamlSequence add(YamlNode node) {
        elements.add(node == null ? YamlNull.INSTANCE : node);
        return this;
    }

    /**
     * Convenience method to add a string value.
     */
    public YamlSequence add(String value) {
        return add(value == null ? YamlNull.INSTANCE : new YamlScalar(value));
    }

    /**
     * Convenience method to add a number value.
     */
    public YamlSequence add(Number value) {
        return add(value == null ? YamlNull.INSTANCE : new YamlScalar(value));
    }

    /**
     * Convenience method to add a boolean value.
     */
    public YamlSequence add(Boolean value) {
        return add(value == null ? YamlNull.INSTANCE : new YamlScalar(value));
    }

    /**
     * Convenience method to add a null value.
     */
    public YamlSequence addNull() {
        elements.add(YamlNull.INSTANCE);
        return this;
    }

    // ============ get ============

    /**
     * @param index the index
     * @return the node at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public YamlNode get(int index) {
        return elements.get(index);
    }

    /**
     * @param index the index
     * @return the node as a YamlMapping
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public YamlMapping getAsMapping(int index) {
        return elements.get(index).asMapping();
    }

    /**
     * @param index the index
     * @return the node as a YamlSequence
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public YamlSequence getAsSequence(int index) {
        return elements.get(index).asSequence();
    }

    /**
     * @param index the index
     * @return the node as a YamlScalar
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public YamlScalar getAsScalar(int index) {
        return elements.get(index).asScalar();
    }

    /**
     * @param index the index
     * @return the string value at the given index
     */
    public String getAsString(int index) {
        return getAsScalar(index).getAsString();
    }

    /**
     * @param index the index
     * @return the int value at the given index
     */
    public int getAsInt(int index) {
        return getAsScalar(index).getAsInt();
    }

    // ============ query ============

    /**
     * @return the number of elements in this sequence
     */
    public int size() {
        return elements.size();
    }

    /**
     * @return true if this sequence is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Replaces the node at the given index.
     * @param index the index
     * @param node the new node
     * @return the previous node
     */
    public YamlNode set(int index, YamlNode node) {
        return elements.set(index, node == null ? YamlNull.INSTANCE : node);
    }

    /**
     * Removes the node at the given index.
     * @param index the index
     * @return the removed node
     */
    public YamlNode remove(int index) {
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
    public List<YamlNode> asList() {
        return new ArrayList<>(elements);
    }

    @Override
    public boolean isSequence() {
        return true;
    }

    @Override
    public YamlSequence asSequence() {
        return this;
    }

    @Override
    public Iterator<YamlNode> iterator() {
        return elements.iterator();
    }

    @Override
    public YamlNode deepCopy() {
        YamlSequence result = new YamlSequence(elements.size());
        for (YamlNode node : elements) {
            result.add(node.deepCopy());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YamlSequence that = (YamlSequence) o;
        return elements.equals(that.elements);
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