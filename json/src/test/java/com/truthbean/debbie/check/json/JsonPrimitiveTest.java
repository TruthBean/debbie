package com.truthbean.debbie.check.json;

import com.truthbean.debbie.json.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
class JsonPrimitiveTest {

    @Test
    @DisplayName("String primitive")
    void testString() {
        var p = new JsonPrimitive("hello");
        assertTrue(p.isString());
        assertFalse(p.isNumber());
        assertFalse(p.isBoolean());
        assertEquals("hello", p.getAsString());
    }

    @Test
    @DisplayName("Integer primitive")
    void testInteger() {
        var p = new JsonPrimitive(42);
        assertTrue(p.isNumber());
        assertEquals(42, p.getAsInt());
        assertEquals(42L, p.getAsLong());
        assertEquals(42.0, p.getAsDouble(), 0.001);
    }

    @Test
    @DisplayName("Double primitive")
    void testDouble() {
        var p = new JsonPrimitive(3.14);
        assertTrue(p.isNumber());
        assertEquals(3.14, p.getAsDouble(), 0.001);
    }

    @Test
    @DisplayName("Long primitive")
    void testLong() {
        var p = new JsonPrimitive(Long.MAX_VALUE);
        assertTrue(p.isNumber());
        assertEquals(Long.MAX_VALUE, p.getAsLong());
    }

    @Test
    @DisplayName("Boolean primitive")
    void testBoolean() {
        var p = new JsonPrimitive(true);
        assertTrue(p.isBoolean());
        assertTrue(p.getAsBoolean());
        assertEquals("true", p.toString());
    }

    @Test
    @DisplayName("BigDecimal from number")
    void testBigDecimal() {
        var p = new JsonPrimitive(new BigDecimal("123456789.123456789"));
        assertEquals(new BigDecimal("123456789.123456789"), p.getAsBigDecimal());
    }

    @Test
    @DisplayName("BigInteger from number")
    void testBigInteger() {
        var p = new JsonPrimitive(new BigInteger("9999999999999999999"));
        assertEquals(new BigInteger("9999999999999999999"), p.getAsBigInteger());
    }

    @Test
    @DisplayName("Null value not allowed")
    void testNullNotAllowed() {
        assertThrows(NullPointerException.class, () -> new JsonPrimitive((String) null));
        assertThrows(NullPointerException.class, () -> new JsonPrimitive((Number) null));
        assertThrows(NullPointerException.class, () -> new JsonPrimitive((Boolean) null));
    }

    @Test
    @DisplayName("Deep copy")
    void testDeepCopy() {
        var p = new JsonPrimitive("hello");
        var copy = p.deepCopy();
        assertNotSame(p, copy);
        assertEquals(p, copy);
    }

    @Test
    @DisplayName("Equals: same value")
    void testEquals() {
        assertEquals(new JsonPrimitive(42), new JsonPrimitive(42));
        assertEquals(new JsonPrimitive("hello"), new JsonPrimitive("hello"));
        assertEquals(new JsonPrimitive(true), new JsonPrimitive(true));
        assertEquals(new JsonPrimitive(42.0), new JsonPrimitive(42.0));
    }

    @Test
    @DisplayName("Equals: int vs long")
    void testEqualsIntLong() {
        assertEquals(new JsonPrimitive(42), new JsonPrimitive(42L));
    }

    @Test
    @DisplayName("Equals: different values")
    void testNotEquals() {
        assertNotEquals(new JsonPrimitive(42), new JsonPrimitive(43));
        assertNotEquals(new JsonPrimitive("hello"), new JsonPrimitive("world"));
        assertNotEquals(new JsonPrimitive(true), new JsonPrimitive(false));
        assertNotEquals(new JsonPrimitive(42), new JsonPrimitive(42.1));
    }

    @Test
    @DisplayName("Not equals null")
    void testNotEqualsNull() {
        assertNotEquals(new JsonPrimitive("test"), null);
    }

    @Test
    @DisplayName("Not equals different type")
    void testNotEqualsDifferentType() {
        assertNotEquals(new JsonPrimitive("test"), "test");
    }

    @Test
    @DisplayName("HashCode consistency")
    void testHashCode() {
        assertEquals(new JsonPrimitive(42).hashCode(), new JsonPrimitive(42).hashCode());
        assertEquals(new JsonPrimitive("hello").hashCode(), new JsonPrimitive("hello").hashCode());
    }

    @Test
    @DisplayName("Get value")
    void testGetValue() {
        assertEquals("hello", new JsonPrimitive("hello").getValue());
        assertEquals(42, new JsonPrimitive(42).getValue());
        assertEquals(true, new JsonPrimitive(true).getValue());
    }

    @Test
    @DisplayName("AsJsonPrimitive returns self")
    void testAsJsonPrimitive() {
        var p = new JsonPrimitive("test");
        assertSame(p, p.asJsonPrimitive());
    }

    @Test
    @DisplayName("String conversion")
    void testStringConversion() {
        assertEquals("hello", new JsonPrimitive("hello").getAsString());
        assertEquals("42", new JsonPrimitive(42).getAsString());
    }
}