package com.truthbean.debbie.check.json;

import com.truthbean.debbie.json.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
class JsonArrayTest {

    @Test
    @DisplayName("Empty array")
    void testEmpty() {
        var arr = new JsonArray();
        assertTrue(arr.isEmpty());
        assertEquals(0, arr.size());
        assertEquals("[]", new JsonSerializer().serialize(arr));
    }

    @Test
    @DisplayName("Add and get primitives")
    void testAddAndGetPrimitives() {
        var arr = new JsonArray();
        arr.add("hello");
        arr.add(42);
        arr.add(3.14);
        arr.add(true);

        assertEquals("hello", arr.getAsString(0));
        assertEquals(42, arr.getAsInt(1));
        assertEquals(3.14, arr.getAsDouble(2), 0.001);
        assertTrue(arr.getAsBoolean(3));
    }

    @Test
    @DisplayName("Add null as JsonNull")
    void testAddNull() {
        var arr = new JsonArray();
        arr.add((String) null);
        arr.add((Number) null);
        arr.add((Boolean) null);
        arr.addNull();

        assertEquals(4, arr.size());
        for (int i = 0; i < 4; i++) {
            assertTrue(arr.get(i).isJsonNull());
        }
    }

    @Test
    @DisplayName("Nested array")
    void testNestedArray() {
        var inner = new JsonArray();
        inner.add(1);
        inner.add(2);

        var outer = new JsonArray();
        outer.add(inner);

        var retrieved = outer.getAsJsonArray(0);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getAsInt(0));
        assertEquals(2, retrieved.getAsInt(1));
    }

    @Test
    @DisplayName("Nested object in array")
    void testObjectInArray() {
        var obj = new JsonObject();
        obj.addProperty("name", "Alice");

        var arr = new JsonArray();
        arr.add(obj);

        var retrieved = arr.getAsJsonObject(0);
        assertEquals("Alice", retrieved.getAsString("name"));
    }

    @Test
    @DisplayName("Set and remove")
    void testSetAndRemove() {
        var arr = new JsonArray();
        arr.add("a");
        arr.add("b");
        arr.add("c");

        assertEquals("b", arr.set(1, new JsonPrimitive("x")).asJsonPrimitive().getAsString());
        assertEquals("x", arr.getAsString(1));

        assertEquals("x", arr.remove(1).asJsonPrimitive().getAsString());
        assertEquals(2, arr.size());
        assertEquals("c", arr.getAsString(1));
    }

    @Test
    @DisplayName("Clear")
    void testClear() {
        var arr = new JsonArray();
        arr.add(1);
        arr.add(2);
        assertFalse(arr.isEmpty());

        arr.clear();
        assertTrue(arr.isEmpty());
    }

    @Test
    @DisplayName("BigDecimal access")
    void testBigDecimal() {
        var arr = new JsonArray();
        arr.add(new BigDecimal("12345.6789"));
        assertEquals(new BigDecimal("12345.6789"), arr.getAsBigDecimal(0));
    }

    @Test
    @DisplayName("As list")
    void testAsList() {
        var arr = new JsonArray();
        arr.add("x");
        arr.add("y");

        List<JsonElement> list = arr.asList();
        assertEquals(2, list.size());
        assertEquals("x", list.get(0).asJsonPrimitive().getAsString());
    }

    @Test
    @DisplayName("Deep copy")
    void testDeepCopy() {
        var inner = new JsonArray();
        inner.add(1);

        var outer = new JsonArray();
        outer.add(inner);

        var copy = (JsonArray) outer.deepCopy();
        assertNotSame(outer, copy);
        assertNotSame(outer.getAsJsonArray(0), copy.getAsJsonArray(0));
        assertEquals(1, copy.getAsJsonArray(0).getAsInt(0));
    }

    @Test
    @DisplayName("Equals and hashCode")
    void testEqualsAndHashCode() {
        var a = new JsonArray();
        a.add(1);
        a.add(2);

        var b = new JsonArray();
        b.add(1);
        b.add(2);

        var c = new JsonArray();
        c.add(1);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "not an array");
    }

    @Test
    @DisplayName("Iterator")
    void testIterator() {
        var arr = new JsonArray();
        arr.add("a");
        arr.add("b");

        int count = 0;
        for (JsonElement element : arr) {
            assertNotNull(element);
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Add null JsonElement as JsonNull")
    void testAddNullJsonElement() {
        var arr = new JsonArray();
        JsonElement nullElement = null;
        arr.add(nullElement);
        assertTrue(arr.get(0).isJsonNull());
    }
}