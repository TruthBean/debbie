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

import java.util.Objects;

/**
 * Represents a JSON null value.
 * <p>
 * This is a singleton class — use {@link #INSTANCE} to access the single instance.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class JsonNull implements JsonElement {

    public static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {
    }

    @Override
    public boolean isJsonNull() {
        return true;
    }

    @Override
    public JsonElement deepCopy() {
        return INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof JsonNull;
    }

    @Override
    public int hashCode() {
        return Objects.hash("JsonNull");
    }

    @Override
    public String toString() {
        return "null";
    }
}