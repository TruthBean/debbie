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
import java.util.Map;

/**
 * Serializes a {@link JsonElement} tree into a JSON string.
 * <p>
 * Supports compact and pretty-print output modes.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class JsonSerializer {

    private final boolean prettyPrint;
    private final String indent;

    /**
     * Creates a serializer with compact output (no whitespace).
     */
    public JsonSerializer() {
        this(false);
    }

    /**
     * @param prettyPrint if true, output is formatted with indentation
     */
    public JsonSerializer(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.indent = "  ";
    }

    /**
     * @param prettyPrint if true, output is formatted
     * @param indent      the indent string (e.g. "  " or "\t")
     */
    public JsonSerializer(boolean prettyPrint, String indent) {
        this.prettyPrint = prettyPrint;
        this.indent = indent;
    }

    /**
     * Serializes a {@link JsonElement} to a JSON string.
     *
     * @param element the element to serialize
     * @return the JSON string
     */
    public String serialize(JsonElement element) {
        StringBuilder sb = new StringBuilder();
        write(element, sb, 0);
        return sb.toString();
    }

    // ============ write ============

    private void write(JsonElement element, StringBuilder sb, int depth) {
        if (element == null || element.isJsonNull()) {
            sb.append("null");
        } else if (element.isJsonObject()) {
            writeObject(element.asJsonObject(), sb, depth);
        } else if (element.isJsonArray()) {
            writeArray(element.asJsonArray(), sb, depth);
        } else if (element.isJsonPrimitive()) {
            writePrimitive(element.asJsonPrimitive(), sb);
        }
    }

    // ============ object ============

    private void writeObject(JsonObject obj, StringBuilder sb, int depth) {
        sb.append('{');
        if (obj.isEmpty()) {
            sb.append('}');
            return;
        }
        if (prettyPrint) {
            sb.append('\n');
        }
        boolean first = true;
        for (Map.Entry<String, JsonElement> entry : obj) {
            if (!first) {
                sb.append(',');
                if (prettyPrint) {
                    sb.append('\n');
                }
            }
            first = false;
            if (prettyPrint) {
                indent(sb, depth + 1);
            }
            writeString(entry.getKey(), sb);
            sb.append(':');
            if (prettyPrint) {
                sb.append(' ');
            }
            write(entry.getValue(), sb, depth + 1);
        }
        if (prettyPrint) {
            sb.append('\n');
            indent(sb, depth);
        }
        sb.append('}');
    }

    // ============ array ============

    private void writeArray(JsonArray array, StringBuilder sb, int depth) {
        sb.append('[');
        if (array.isEmpty()) {
            sb.append(']');
            return;
        }
        if (prettyPrint) {
            sb.append('\n');
        }
        boolean first = true;
        for (JsonElement element : array) {
            if (!first) {
                sb.append(',');
                if (prettyPrint) {
                    sb.append('\n');
                }
            }
            first = false;
            if (prettyPrint) {
                indent(sb, depth + 1);
            }
            write(element, sb, depth + 1);
        }
        if (prettyPrint) {
            sb.append('\n');
            indent(sb, depth);
        }
        sb.append(']');
    }

    // ============ primitive ============

    private void writePrimitive(JsonPrimitive primitive, StringBuilder sb) {
        if (primitive.isString()) {
            writeString(primitive.getAsString(), sb);
        } else if (primitive.isBoolean()) {
            sb.append(primitive.getAsBoolean() ? "true" : "false");
        } else if (primitive.isNumber()) {
            Number num = primitive.getAsNumber();
            if (num instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) num;
                sb.append(bd.toPlainString());
            } else {
                sb.append(num.toString());
            }
        }
    }

    // ============ string escaping ============

    private void writeString(String str, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"' : sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    private void indent(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append(indent);
        }
    }
}