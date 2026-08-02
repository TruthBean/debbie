package com.truthbean.debbie.check.json;

import com.truthbean.debbie.json.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
class JsonSerializerTest {

    private final JsonSerializer serializer = new JsonSerializer();
    private final JsonSerializer prettyPrinter = new JsonSerializer(true);

    // ============ primitives ============

    @Test
    @DisplayName("Serialize null")
    void testNull() {
        assertEquals("null", serializer.serialize(JsonNull.INSTANCE));
    }

    @Test
    @DisplayName("Serialize string")
    void testString() {
        assertEquals("\"hello\"", serializer.serialize(new JsonPrimitive("hello")));
    }

    @Test
    @DisplayName("Serialize number")
    void testNumber() {
        assertEquals("42", serializer.serialize(new JsonPrimitive(42)));
        assertEquals("3.14", serializer.serialize(new JsonPrimitive(3.14)));
    }

    @Test
    @DisplayName("Serialize boolean")
    void testBoolean() {
        assertEquals("true", serializer.serialize(new JsonPrimitive(true)));
        assertEquals("false", serializer.serialize(new JsonPrimitive(false)));
    }

    @Test
    @DisplayName("Serialize big decimal without scientific notation")
    void testBigDecimal() {
        var bd = new BigDecimal("12345678901234567890.12345");
        assertEquals("12345678901234567890.12345", serializer.serialize(new JsonPrimitive(bd)));
    }

    // ============ string escaping ============

    @Test
    @DisplayName("Escape special characters in string")
    void testEscapeString() {
        var json = serializer.serialize(new JsonPrimitive("\"\\\n\r\t\b\f"));
        assertTrue(json.contains("\\\""));
        assertTrue(json.contains("\\\\"));
        assertTrue(json.contains("\\n"));
        assertTrue(json.contains("\\r"));
        assertTrue(json.contains("\\t"));
        assertTrue(json.contains("\\b"));
        assertTrue(json.contains("\\f"));
    }

    @Test
    @DisplayName("Escape control characters")
    void testEscapeControlChar() {
        var json = serializer.serialize(new JsonPrimitive("\u0001\u0002"));
        assertTrue(json.contains("\\u0001"));
        assertTrue(json.contains("\\u0002"));
    }

    // ============ object ============

    @Test
    @DisplayName("Serialize empty object")
    void testEmptyObject() {
        assertEquals("{}", serializer.serialize(new JsonObject()));
    }

    @Test
    @DisplayName("Serialize simple object")
    void testSimpleObject() {
        var obj = new JsonObject();
        obj.addProperty("name", "Alice");
        obj.addProperty("age", 30);

        var json = serializer.serialize(obj);
        assertTrue(json.contains("\"name\":\"Alice\""));
        assertTrue(json.contains("\"age\":30"));
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
    }

    @Test
    @DisplayName("Serialize object with null")
    void testObjectWithNull() {
        var obj = new JsonObject();
        obj.addProperty("key", (String) null);
        assertEquals("{\"key\":null}", serializer.serialize(obj));
    }

    @Test
    @DisplayName("Serialize nested object")
    void testNestedObject() {
        var inner = new JsonObject();
        inner.addProperty("x", "1");

        var outer = new JsonObject();
        outer.add("inner", inner);

        var json = serializer.serialize(outer);
        assertTrue(json.contains("\"inner\":{\"x\":\"1\"}"));
    }

    // ============ array ============

    @Test
    @DisplayName("Serialize empty array")
    void testEmptyArray() {
        assertEquals("[]", serializer.serialize(new JsonArray()));
    }

    @Test
    @DisplayName("Serialize array")
    void testArray() {
        var arr = new JsonArray();
        arr.add(1);
        arr.add(2);
        arr.add(3);
        assertEquals("[1,2,3]", serializer.serialize(arr));
    }

    @Test
    @DisplayName("Serialize mixed array")
    void testMixedArray() {
        var arr = new JsonArray();
        arr.add("hello");
        arr.add(42);
        arr.add(true);
        arr.addNull();
        var json = serializer.serialize(arr);
        assertTrue(json.contains("\"hello\""));
        assertTrue(json.contains("42"));
        assertTrue(json.contains("true"));
        assertTrue(json.contains("null"));
    }

    // ============ pretty print ============

    @Test
    @DisplayName("Pretty print object")
    void testPrettyPrintObject() {
        var obj = new JsonObject();
        obj.addProperty("a", "1");
        obj.addProperty("b", "2");

        var json = prettyPrinter.serialize(obj);
        assertTrue(json.contains("\n"));
        assertTrue(json.contains("  "));
        assertTrue(json.startsWith("{\n"));
        assertTrue(json.endsWith("\n}"));
    }

    @Test
    @DisplayName("Pretty print nested")
    void testPrettyPrintNested() {
        var inner = new JsonObject();
        inner.addProperty("x", "1");

        var outer = new JsonObject();
        outer.add("inner", inner);

        var json = prettyPrinter.serialize(outer);
        assertTrue(json.contains("  \"inner\": {\n    \"x\": \"1\"\n  }\n"));
    }

    @Test
    @DisplayName("Pretty print array")
    void testPrettyPrintArray() {
        var arr = new JsonArray();
        arr.add(1);
        arr.add(2);

        var json = prettyPrinter.serialize(arr);
        assertTrue(json.contains("\n"));
        assertTrue(json.contains("  "));
    }

    @Test
    @DisplayName("Pretty print empty object")
    void testPrettyPrintEmptyObject() {
        assertEquals("{}", prettyPrinter.serialize(new JsonObject()));
    }

    @Test
    @DisplayName("Pretty print empty array")
    void testPrettyPrintEmptyArray() {
        assertEquals("[]", prettyPrinter.serialize(new JsonArray()));
    }

    // ============ round-trip with parser ============

    @Test
    @DisplayName("Serialize re-parsed JSON produces same structure")
    void testSerializeRoundTrip() {
        var original = "{\"name\":\"Alice\",\"age\":30,\"active\":true,\"data\":null,\"scores\":[1,2,3]}";
        var element = JsonParser.parse(original);
        var json = serializer.serialize(element);
        var reparsed = JsonParser.parse(json);
        assertEquals(element, reparsed);
    }

    @Test
    @DisplayName("Custom indent")
    void testCustomIndent() {
        var serializer = new JsonSerializer(true, "\t");
        var obj = new JsonObject();
        obj.addProperty("a", "1");

        var json = serializer.serialize(obj);
        assertTrue(json.contains("\t"));
    }
}