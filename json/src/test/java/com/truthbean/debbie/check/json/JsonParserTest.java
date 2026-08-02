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
class JsonParserTest {

    // ============ simple values ============

    @Test
    @DisplayName("Parse null")
    void testNull() {
        assertTrue(JsonParser.parse("null").isJsonNull());
    }

    @Test
    @DisplayName("Parse true")
    void testTrue() {
        var p = JsonParser.parse("true").asJsonPrimitive();
        assertTrue(p.getAsBoolean());
    }

    @Test
    @DisplayName("Parse false")
    void testFalse() {
        var p = JsonParser.parse("false").asJsonPrimitive();
        assertFalse(p.getAsBoolean());
    }

    @Test
    @DisplayName("Parse string")
    void testString() {
        var p = JsonParser.parse("\"hello\"").asJsonPrimitive();
        assertEquals("hello", p.getAsString());
    }

    @Test
    @DisplayName("Parse empty string")
    void testEmptyString() {
        var p = JsonParser.parse("\"\"").asJsonPrimitive();
        assertEquals("", p.getAsString());
    }

    @Test
    @DisplayName("Parse integer")
    void testInteger() {
        var p = JsonParser.parse("42").asJsonPrimitive();
        assertEquals(42, p.getAsInt());
    }

    @Test
    @DisplayName("Parse negative integer")
    void testNegativeInteger() {
        var p = JsonParser.parse("-42").asJsonPrimitive();
        assertEquals(-42, p.getAsInt());
    }

    @Test
    @DisplayName("Parse zero")
    void testZero() {
        var p = JsonParser.parse("0").asJsonPrimitive();
        assertEquals(0, p.getAsInt());
    }

    @Test
    @DisplayName("Parse decimal")
    void testDecimal() {
        var p = JsonParser.parse("3.14").asJsonPrimitive();
        assertEquals(3.14, p.getAsDouble(), 0.001);
    }

    @Test
    @DisplayName("Parse scientific notation")
    void testScientificNotation() {
        var p = JsonParser.parse("1.5e10").asJsonPrimitive();
        assertEquals(1.5e10, p.getAsDouble(), 0.001);
    }

    @Test
    @DisplayName("Parse negative exponent")
    void testNegativeExponent() {
        var p = JsonParser.parse("1.5E-3").asJsonPrimitive();
        assertEquals(0.0015, p.getAsDouble(), 0.000001);
    }

    @Test
    @DisplayName("Parse large integer as long")
    void testLargeInteger() {
        var p = JsonParser.parse("2147483648").asJsonPrimitive();
        assertEquals(2147483648L, p.getAsLong());
    }

    @Test
    @DisplayName("Parse big decimal")
    void testBigDecimal() {
        var p = JsonParser.parse("12345678901234567890.12345").asJsonPrimitive();
        assertEquals(new BigDecimal("12345678901234567890.12345"), p.getAsBigDecimal());
    }

    @Test
    @DisplayName("Parse string with escaped characters")
    void testEscapedString() {
        var p = JsonParser.parse("\"hello\\nworld\"").asJsonPrimitive();
        assertEquals("hello\nworld", p.getAsString());
    }

    @Test
    @DisplayName("Parse string with unicode escape")
    void testUnicodeEscape() {
        var p = JsonParser.parse("\"\\u0048\\u0065\\u006C\\u006C\\u006F\"").asJsonPrimitive();
        assertEquals("Hello", p.getAsString());
    }

    @Test
    @DisplayName("Parse string with all escape chars")
    void testAllEscapeChars() {
        var p = JsonParser.parse("\"\\\\\\\"\\/\\b\\f\\n\\r\\t\"").asJsonPrimitive();
        assertEquals("\\\"\b\f\n\r\t", p.getAsString());
    }

    // ============ objects ============

    @Test
    @DisplayName("Parse empty object")
    void testEmptyObject() {
        var obj = JsonParser.parse("{}").asJsonObject();
        assertNotNull(obj);
        assertTrue(obj.isEmpty());
    }

    @Test
    @DisplayName("Parse simple object")
    void testSimpleObject() {
        var obj = JsonParser.parse("{\"name\":\"Alice\",\"age\":30}").asJsonObject();
        assertEquals("Alice", obj.getAsString("name"));
        assertEquals(30, obj.getAsInt("age"));
    }

    @Test
    @DisplayName("Parse object with all value types")
    void testObjectAllTypes() {
        var json = "{\"str\":\"hello\",\"num\":42,\"dec\":3.14,\"flag\":true,\"n\":null}";
        var obj = JsonParser.parse(json).asJsonObject();

        assertEquals("hello", obj.getAsString("str"));
        assertEquals(42, obj.getAsInt("num"));
        assertEquals(3.14, obj.getAsDouble("dec"), 0.001);
        assertTrue(obj.getAsBoolean("flag"));
        assertTrue(obj.get("n").isJsonNull());
    }

    @Test
    @DisplayName("Parse nested object")
    void testNestedObject() {
        var json = "{\"outer\":{\"inner\":\"value\"}}";
        var obj = JsonParser.parse(json).asJsonObject();
        var inner = obj.getAsJsonObject("outer");
        assertNotNull(inner);
        assertEquals("value", inner.getAsString("inner"));
    }

    @Test
    @DisplayName("Parse object with whitespace")
    void testObjectWithWhitespace() {
        var json = "  {  \"key\"  :  \"value\"  }  ";
        var obj = JsonParser.parse(json).asJsonObject();
        assertEquals("value", obj.getAsString("key"));
    }

    @Test
    @DisplayName("Parse object with empty string key")
    void testObjectWithEmptyKey() {
        var obj = JsonParser.parse("{\"\":\"empty\"}").asJsonObject();
        assertEquals("empty", obj.getAsString(""));
    }

    @Test
    @DisplayName("Parse object with multiple keys")
    void testObjectMultipleKeys() {
        var json = "{\"a\":1,\"b\":2,\"c\":3}";
        var obj = JsonParser.parse(json).asJsonObject();
        assertEquals(3, obj.size());
        assertEquals(1, obj.getAsInt("a"));
        assertEquals(2, obj.getAsInt("b"));
        assertEquals(3, obj.getAsInt("c"));
    }

    // ============ arrays ============

    @Test
    @DisplayName("Parse empty array")
    void testEmptyArray() {
        var arr = JsonParser.parse("[]").asJsonArray();
        assertNotNull(arr);
        assertTrue(arr.isEmpty());
    }

    @Test
    @DisplayName("Parse simple array")
    void testSimpleArray() {
        var arr = JsonParser.parse("[1,2,3]").asJsonArray();
        assertEquals(3, arr.size());
        assertEquals(1, arr.getAsInt(0));
        assertEquals(2, arr.getAsInt(1));
        assertEquals(3, arr.getAsInt(2));
    }

    @Test
    @DisplayName("Parse mixed array")
    void testMixedArray() {
        var json = "[\"hello\",42,true,null,3.14]";
        var arr = JsonParser.parse(json).asJsonArray();
        assertEquals(5, arr.size());
        assertEquals("hello", arr.getAsString(0));
        assertEquals(42, arr.getAsInt(1));
        assertTrue(arr.getAsBoolean(2));
        assertTrue(arr.get(3).isJsonNull());
        assertEquals(3.14, arr.getAsDouble(4), 0.001);
    }

    @Test
    @DisplayName("Parse nested array")
    void testNestedArray() {
        var json = "[[1,2],[3,4]]";
        var arr = JsonParser.parse(json).asJsonArray();
        assertEquals(2, arr.size());
        assertEquals(1, arr.getAsJsonArray(0).getAsInt(0));
        assertEquals(4, arr.getAsJsonArray(1).getAsInt(1));
    }

    @Test
    @DisplayName("Parse array of objects")
    void testArrayOfObjects() {
        var json = "[{\"x\":1},{\"x\":2}]";
        var arr = JsonParser.parse(json).asJsonArray();
        assertEquals(2, arr.size());
        assertEquals(1, arr.getAsJsonObject(0).getAsInt("x"));
        assertEquals(2, arr.getAsJsonObject(1).getAsInt("x"));
    }

    // ============ round-trip ============

    @Test
    @DisplayName("Parse and serialize round-trip")
    void testRoundTrip() {
        var json = "{\"name\":\"Alice\",\"age\":30,\"scores\":[90,85,95],\"address\":{\"city\":\"Beijing\"}}";
        var parsed = JsonParser.parse(json);
        var serialized = new JsonSerializer().serialize(parsed);
        var reparsed = JsonParser.parse(serialized);
        assertEquals(parsed, reparsed);
    }

    // ============ error cases ============

    @Test
    @DisplayName("Reject malformed JSON: missing closing brace")
    void testMissingClosingBrace() {
        assertThrows(JsonException.class, () -> JsonParser.parse("{\"key\":\"value\""));
    }

    @Test
    @DisplayName("Reject malformed JSON: missing closing bracket")
    void testMissingClosingBracket() {
        assertThrows(JsonException.class, () -> JsonParser.parse("[1,2,3"));
    }

    @Test
    @DisplayName("Reject malformed JSON: unterminated string")
    void testUnterminatedString() {
        assertThrows(JsonException.class, () -> JsonParser.parse("\"hello"));
    }

    @Test
    @DisplayName("Reject malformed JSON: trailing comma")
    void testTrailingComma() {
        assertThrows(JsonException.class, () -> JsonParser.parse("{\"a\":1,}"));
        assertThrows(JsonException.class, () -> JsonParser.parse("[1,2,]"));
    }

    @Test
    @DisplayName("Reject malformed JSON: invalid character")
    void testInvalidCharacter() {
        assertThrows(JsonException.class, () -> JsonParser.parse("{invalid}"));
    }

    @Test
    @DisplayName("Reject malformed JSON: single quotes")
    void testSingleQuotes() {
        assertThrows(JsonException.class, () -> JsonParser.parse("{'key':'value'}"));
    }

    @Test
    @DisplayName("Reject malformed JSON: trailing characters")
    void testTrailingCharacters() {
        assertThrows(JsonException.class, () -> JsonParser.parse("{} extra"));
    }

    @Test
    @DisplayName("Reject malformed JSON: invalid number")
    void testInvalidNumber() {
        assertThrows(JsonException.class, () -> JsonParser.parse("12a"));
        assertThrows(JsonException.class, () -> JsonParser.parse("-"));
        assertThrows(JsonException.class, () -> JsonParser.parse("."));
    }

    @Test
    @DisplayName("Reject malformed JSON: invalid escape")
    void testInvalidEscape() {
        assertThrows(JsonException.class, () -> JsonParser.parse("\"\\x\""));
    }

    @Test
    @DisplayName("Reject malformed JSON: unescaped control char")
    void testUnescapedControlChar() {
        assertThrows(JsonException.class, () -> JsonParser.parse("\"" + (char) 0x01 + "\""));
    }

    @Test
    @DisplayName("Reject null input")
    void testNullInput() {
        assertThrows(NullPointerException.class, () -> JsonParser.parse(null));
    }

    @Test
    @DisplayName("Reject empty string")
    void testEmptyInput() {
        assertThrows(JsonException.class, () -> JsonParser.parse(""));
    }

    @Test
    @DisplayName("Reject whitespace-only input")
    void testWhitespaceInput() {
        assertThrows(JsonException.class, () -> JsonParser.parse("   "));
    }

    @Test
    @DisplayName("Reject invalid boolean value")
    void testInvalidBoolean() {
        assertThrows(JsonException.class, () -> JsonParser.parse("tru"));
        assertThrows(JsonException.class, () -> JsonParser.parse("fals"));
    }

    @Test
    @DisplayName("Reject invalid null value")
    void testInvalidNull() {
        assertThrows(JsonException.class, () -> JsonParser.parse("nul"));
    }

    @Test
    @DisplayName("Reject number with leading zero")
    void testLeadingZero() {
        assertThrows(JsonException.class, () -> JsonParser.parse("0123"));
    }

    @Test
    @DisplayName("Reject number with trailing decimal point")
    void testTrailingDecimal() {
        assertThrows(JsonException.class, () -> JsonParser.parse("42."));
    }

    @Test
    @DisplayName("Reject number with leading decimal point")
    void testLeadingDecimal() {
        assertThrows(JsonException.class, () -> JsonParser.parse(".5"));
    }
}