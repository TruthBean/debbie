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

/**
 * Base interface for all YAML node types.
 * <p>
 * Represents a node in a YAML document tree. Each element can be
 * one of: {@link YamlMapping}, {@link YamlSequence}, {@link YamlScalar}, or {@link YamlNull}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface YamlNode {

    /**
     * @return true if this node is a YAML mapping
     */
    default boolean isMapping() {
        return false;
    }

    /**
     * @return true if this node is a YAML sequence
     */
    default boolean isSequence() {
        return false;
    }

    /**
     * @return true if this node is a YAML scalar
     */
    default boolean isScalar() {
        return false;
    }

    /**
     * @return true if this node is YAML null
     */
    default boolean isNull() {
        return false;
    }

    /**
     * @return this cast to YamlMapping, or null if not a mapping
     */
    default YamlMapping asMapping() {
        return null;
    }

    /**
     * @return this cast to YamlSequence, or null if not a sequence
     */
    default YamlSequence asSequence() {
        return null;
    }

    /**
     * @return this cast to YamlScalar, or null if not a scalar
     */
    default YamlScalar asScalar() {
        return null;
    }

    /**
     * @return a deep copy of this node
     */
    YamlNode deepCopy();

    /**
     * @return the YAML string representation
     */
    @Override
    String toString();
}