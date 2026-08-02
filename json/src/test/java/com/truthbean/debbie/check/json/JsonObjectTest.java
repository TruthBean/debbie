package com.truthbean.debbie.check.json;

import com.truthbean.debbie.json.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
class JsonObjectTest {

    @Test
    @DisplayName("Empty object")
    void testEmpty() {
        var obj = new JsonObject();
        assertTrue(obj.isEmpty());
        assertEquals(0, obj.size());
        assertEquals("{}", new JsonSerializer().serialize(obj));
    }

    @Test
    @DisplayName("Add and get primitive values")
    void testAddAndGetPrimitives() {
        var obj = new JsonObject();
        obj.addProperty("name", "Alice");
        obj.addProperty("age", 25);
        obj.addProperty("height", 1.75);
        obj.addProperty("active", true);
        obj.addNull("comment");

        assertEquals("Alice", obj.getAsString("name"));
        assertEquals(25, obj.getAsInt("age"));
        assertEquals(1.75, obj.getAsDouble("height"), 0.001);
        assertTrue(obj.getAsBoolean("active"));
        assertTrue(obj.get("comment").isJsonNull());
    }

    @Test
    @DisplayName("Nested object")
    void testNestedObject() {
        var address = new JsonObject();
        address.addProperty("city", "Beijing");
        address.addProperty("zip", "100000");

        var obj = new JsonObject();
        obj.add("address", address);

        var retrieved = obj.getAsJsonObject("address");
        assertNotNull(retrieved);
        assertEquals("Beijing", retrieved.getAsString("city"));
        assertEquals("100000", retrieved.getAsString("zip"));
    }

    @Test
    @DisplayName("Has and remove")
    void testHasAndRemove() {
        var obj = new JsonObject();
        obj.addProperty("a", "1");
        obj.addProperty("b", "2");

        assertTrue(obj.has("a"));
        assertFalse(obj.has("c"));

        assertNotNull(obj.remove("a"));
        assertFalse(obj.has("a"));
        assertEquals(1, obj.size());
    }

    @Test
    @DisplayName("Clear")
    void testClear() {
        var obj = new JsonObject();
        obj.addProperty("a", "1");
        obj.addProperty("b", "2");
        assertFalse(obj.isEmpty());

        obj.clear();
        assertTrue(obj.isEmpty());
    }

    @Test
    @DisplayName("Key set")
    void testKeySet() {
        var obj = new JsonObject();
        obj.addProperty("x", "10");
        obj.addProperty("y", "20");
        obj.addProperty("z", "30");

        var keys = obj.keySet();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("x"));
        assertTrue(keys.contains("y"));
        assertTrue(keys.contains("z"));
    }

    @Test
    @DisplayName("Null value handling")
    void testNullValue() {
        var obj = new JsonObject();
        obj.addProperty("a", (String) null);
        obj.addProperty("b", (Number) null);
        obj.addProperty("c", (Boolean) null);

        assertTrue(obj.get("a").isJsonNull());
        assertTrue(obj.get("b").isJsonNull());
        assertTrue(obj.get("c").isJsonNull());
    }

    @Test
    @DisplayName("BigDecimal and BigInteger")
    void testBigNumbers() {
        var obj = new JsonObject();
        obj.addProperty("bd", new BigDecimal("12345678901234567890.12345"));
        obj.addProperty("bi", new BigInteger("9999999999999999999"));

        assertEquals(new BigDecimal("12345678901234567890.12345"), obj.getAsBigDecimal("bd"));
        assertEquals(new BigInteger("9999999999999999999"), obj.getAsBigInteger("bi"));
    }

    @Test
    @DisplayName("Iteration order preserved")
    void testIterationOrder() {
        var obj = new JsonObject();
        obj.addProperty("first", "1");
        obj.addProperty("second", "2");
        obj.addProperty("third", "3");

        var keys = obj.keySet().toArray(new String[0]);
        assertEquals("first", keys[0]);
        assertEquals("second", keys[1]);
        assertEquals("third", keys[2]);
    }

    @Test
    @DisplayName("Deep copy")
    void testDeepCopy() {
        var inner = new JsonObject();
        inner.addProperty("x", "10");

        var obj = new JsonObject();
        obj.add("inner", inner);

        var copy = (JsonObject) obj.deepCopy();
        assertNotSame(obj, copy);
        assertNotSame(obj.getAsJsonObject("inner"), copy.getAsJsonObject("inner"));
        assertEquals("10", copy.getAsJsonObject("inner").getAsString("x"));
    }

    @Test
    @DisplayName("Equals and hashCode")
    void testEqualsAndHashCode() {
        var a = new JsonObject();
        a.addProperty("name", "Alice");

        var b = new JsonObject();
        b.addProperty("name", "Alice");

        var c = new JsonObject();
        c.addProperty("name", "Bob");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "not an object");
    }

    @Test
    @DisplayName("Add null JsonElement as JsonNull")
    void testAddNullJsonElement() {
        var obj = new JsonObject();
        obj.add("key", null);
        assertTrue(obj.get("key").isJsonNull());
    }

    @Test
    @DisplayName("Iterator")
    void testIterator() {
        var obj = new JsonObject();
        obj.addProperty("a", "1");
        obj.addProperty("b", "2");

        int count = 0;
        for (Map.Entry<String, JsonElement> entry : obj) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            count++;
        }
        assertEquals(2, count);
    }
}