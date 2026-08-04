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

import java.util.Objects;

/**
 * Represents a YAML null value.
 * <p>
 * This is a singleton class — use {@link #INSTANCE} to access the single instance.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class YamlNull implements YamlNode {

    public static final YamlNull INSTANCE = new YamlNull();

    private YamlNull() {
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public YamlNode deepCopy() {
        return INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof YamlNull;
    }

    @Override
    public int hashCode() {
        return Objects.hash("YamlNull");
    }

    @Override
    public String toString() {
        return "null";
    }
}