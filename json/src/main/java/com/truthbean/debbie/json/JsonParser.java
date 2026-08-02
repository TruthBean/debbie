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
import java.util.Objects;

/**
 * Recursive descent JSON parser.
 * <p>
 * Parses a JSON string into a {@link JsonElement} tree.
 * Supports all standard JSON value types: objects, arrays, strings, numbers,
 * booleans, and null.
 * <p>
 * This parser is strict and follows the JSON specification (RFC 7159).
 * It does not support comments, trailing commas, or single-quoted strings.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class JsonParser {

    private final String json;
    private int pos;

    private JsonParser(String json) {
        this.json = Objects.requireNonNull(json, "json must not be null");
        this.pos = 0;
    }

    /**
     * Parses a JSON string into a {@link JsonElement}.
     *
     * @param json the JSON string to parse
     * @return the parsed JsonElement
     * @throws JsonException if the input is not valid JSON
     */
    public static JsonElement parse(String json) {
        JsonParser parser = new JsonParser(json);
        JsonElement result = parser.readValue();
        parser.skipWhitespace();
        if (parser.pos < parser.json.length()) {
            throw new JsonException("Unexpected trailing characters at position " + parser.pos);
        }
        return result;
    }

    // ============ value reading ============

    private JsonElement readValue() {
        skipWhitespace();
        if (pos >= json.length()) {
            throw new JsonException("Unexpected end of JSON input");
        }
        char c = json.charAt(pos);
        switch (c) {
            case '{':
                return readObject();
            case '[':
                return readArray();
            case '"':
                return new JsonPrimitive(readString());
            case 't':
            case 'f':
                return readBoolean();
            case 'n':
                return readNull();
            default:
                if (c == '-' || (c >= '0' && c <= '9')) {
                    return readNumber();
                }
                throw new JsonException("Unexpected character '" + c + "' at position " + pos);
        }
    }

    // ============ object ============

    private JsonObject readObject() {
        expect('{');
        JsonObject result = new JsonObject();
        skipWhitespace();
        if (pos < json.length() && json.charAt(pos) == '}') {
            pos++;
            return result;
        }
        while (true) {
            skipWhitespace();
            if (pos >= json.length()) {
                throw new JsonException("Unterminated JSON object");
            }
            // key
            if (json.charAt(pos) != '"') {
                throw new JsonException("Expected string key at position " + pos);
            }
            String key = readString();
            // colon
            skipWhitespace();
            expect(':');
            // value
            skipWhitespace();
            JsonElement value = readValue();
            result.add(key, value);
            // comma or end
            skipWhitespace();
            if (pos < json.length() && json.charAt(pos) == ',') {
                pos++;
                continue;
            }
            if (pos < json.length() && json.charAt(pos) == '}') {
                pos++;
                return result;
            }
            throw new JsonException("Expected ',' or '}' at position " + pos);
        }
    }

    // ============ array ============

    private JsonArray readArray() {
        expect('[');
        JsonArray result = new JsonArray();
        skipWhitespace();
        if (pos < json.length() && json.charAt(pos) == ']') {
            pos++;
            return result;
        }
        while (true) {
            skipWhitespace();
            result.add(readValue());
            skipWhitespace();
            if (pos < json.length() && json.charAt(pos) == ',') {
                pos++;
                continue;
            }
            if (pos < json.length() && json.charAt(pos) == ']') {
                pos++;
                return result;
            }
            throw new JsonException("Expected ',' or ']' at position " + pos);
        }
    }

    // ============ string ============

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == '"') {
                pos++;
                return sb.toString();
            }
            if (c == '\\') {
                pos++;
                if (pos >= json.length()) {
                    throw new JsonException("Unexpected end of string escape");
                }
                char escaped = json.charAt(pos);
                switch (escaped) {
                    case '"' : sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/' : sb.append('/'); break;
                    case 'b' : sb.append('\b'); break;
                    case 'f' : sb.append('\f'); break;
                    case 'n' : sb.append('\n'); break;
                    case 'r' : sb.append('\r'); break;
                    case 't' : sb.append('\t'); break;
                    case 'u' :
                        sb.append(readUnicodeEscape());
                        break;
                    default:
                        throw new JsonException("Invalid escape character '\\" + escaped + "' at position " + pos);
                }
                pos++;
            } else {
                if (c < 0x20) {
                    throw new JsonException("Unescaped control character in string at position " + pos);
                }
                sb.append(c);
                pos++;
            }
        }
        throw new JsonException("Unterminated JSON string");
    }

    private char readUnicodeEscape() {
        if (pos + 4 >= json.length()) {
            throw new JsonException("Unexpected end of unicode escape");
        }
        String hex = json.substring(pos + 1, pos + 5);
        pos += 4;
        try {
            return (char) Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            throw new JsonException("Invalid unicode escape '\\u" + hex + "' at position " + (pos - 4));
        }
    }

    // ============ number ============

    private JsonPrimitive readNumber() {
        int start = pos;
        if (pos < json.length() && json.charAt(pos) == '-') {
            pos++;
        }
        // integer part
        if (pos < json.length() && json.charAt(pos) == '0') {
            pos++;
        } else if (pos < json.length() && json.charAt(pos) >= '1' && json.charAt(pos) <= '9') {
            pos++;
            while (pos < json.length() && json.charAt(pos) >= '0' && json.charAt(pos) <= '9') {
                pos++;
            }
        } else {
            throw new JsonException("Invalid number at position " + start);
        }
        // fraction
        boolean hasFraction = false;
        if (pos < json.length() && json.charAt(pos) == '.') {
            hasFraction = true;
            pos++;
            if (pos >= json.length() || json.charAt(pos) < '0' || json.charAt(pos) > '9') {
                throw new JsonException("Invalid number at position " + start);
            }
            while (pos < json.length() && json.charAt(pos) >= '0' && json.charAt(pos) <= '9') {
                pos++;
            }
        }
        // exponent
        boolean hasExponent = false;
        if (pos < json.length() && (json.charAt(pos) == 'e' || json.charAt(pos) == 'E')) {
            hasExponent = true;
            pos++;
            if (pos < json.length() && (json.charAt(pos) == '+' || json.charAt(pos) == '-')) {
                pos++;
            }
            if (pos >= json.length() || json.charAt(pos) < '0' || json.charAt(pos) > '9') {
                throw new JsonException("Invalid number at position " + start);
            }
            while (pos < json.length() && json.charAt(pos) >= '0' && json.charAt(pos) <= '9') {
                pos++;
            }
        }
        String numberStr = json.substring(start, pos);
        if (hasFraction || hasExponent) {
            return new JsonPrimitive(new BigDecimal(numberStr));
        }
        long longVal = Long.parseLong(numberStr);
        if (longVal >= Integer.MIN_VALUE && longVal <= Integer.MAX_VALUE) {
            return new JsonPrimitive((int) longVal);
        }
        return new JsonPrimitive(longVal);
    }

    // ============ boolean / null ============

    private JsonPrimitive readBoolean() {
        if (json.startsWith("true", pos)) {
            pos += 4;
            return new JsonPrimitive(true);
        }
        if (json.startsWith("false", pos)) {
            pos += 5;
            return new JsonPrimitive(false);
        }
        throw new JsonException("Invalid value at position " + pos + ": expected true or false");
    }

    private JsonNull readNull() {
        if (json.startsWith("null", pos)) {
            pos += 4;
            return JsonNull.INSTANCE;
        }
        throw new JsonException("Invalid value at position " + pos + ": expected null");
    }

    // ============ helpers ============

    private void expect(char c) {
        if (pos >= json.length() || json.charAt(pos) != c) {
            throw new JsonException("Expected '" + c + "' at position " + pos + " but found '" +
                    (pos < json.length() ? json.charAt(pos) : "EOF") + "'");
        }
        pos++;
    }

    private void skipWhitespace() {
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                pos++;
            } else {
                break;
            }
        }
    }
}