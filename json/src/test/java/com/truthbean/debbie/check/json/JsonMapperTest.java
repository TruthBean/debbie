package com.truthbean.debbie.check.json;

import com.truthbean.debbie.json.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
class JsonMapperTest {

    // ============ toJsonElement: primitives ============

    @Test
    @DisplayName("Null to JsonElement")
    void testNullToJson() {
        assertTrue(JsonMapper.toJsonElement(null).isJsonNull());
    }

    @Test
    @DisplayName("String to JsonElement")
    void testStringToJson() {
        var p = JsonMapper.toJsonElement("hello").asJsonPrimitive();
        assertEquals("hello", p.getAsString());
    }

    @Test
    @DisplayName("Number to JsonElement")
    void testNumberToJson() {
        assertEquals(42, JsonMapper.toJsonElement(42).asJsonPrimitive().getAsInt());
        assertEquals(3.14, JsonMapper.toJsonElement(3.14).asJsonPrimitive().getAsDouble(), 0.001);
    }

    @Test
    @DisplayName("Boolean to JsonElement")
    void testBooleanToJson() {
        assertTrue(JsonMapper.toJsonElement(true).asJsonPrimitive().getAsBoolean());
    }

    @Test
    @DisplayName("Character to JsonElement")
    void testCharToJson() {
        assertEquals("A", JsonMapper.toJsonElement('A').asJsonPrimitive().getAsString());
    }

    // ============ toJsonElement: collections ============

    @Test
    @DisplayName("List to JsonArray")
    void testListToJson() {
        var list = List.of("a", "b", "c");
        var arr = JsonMapper.toJsonElement(list).asJsonArray();
        assertEquals(3, arr.size());
        assertEquals("a", arr.getAsString(0));
        assertEquals("b", arr.getAsString(1));
        assertEquals("c", arr.getAsString(2));
    }

    @Test
    @DisplayName("Map to JsonObject")
    void testMapToJson() {
        var map = new LinkedHashMap<String, Object>();
        map.put("name", "Alice");
        map.put("age", 30);

        var obj = JsonMapper.toJsonElement(map).asJsonObject();
        assertEquals("Alice", obj.getAsString("name"));
        assertEquals(30, obj.getAsInt("age"));
    }

    @Test
    @DisplayName("Array to JsonArray")
    void testArrayToJson() {
        var arr = JsonMapper.toJsonElement(new int[]{1, 2, 3}).asJsonArray();
        assertEquals(3, arr.size());
        assertEquals(1, arr.getAsInt(0));
        assertEquals(2, arr.getAsInt(1));
        assertEquals(3, arr.getAsInt(2));
    }

    @Test
    @DisplayName("Nested collection to JsonElement")
    void testNestedCollectionToJson() {
        var map = new LinkedHashMap<String, Object>();
        map.put("items", List.of(1, 2, 3));
        map.put("nested", Map.of("key", "value"));

        var obj = JsonMapper.toJsonElement(map).asJsonObject();
        assertEquals(3, obj.getAsJsonArray("items").size());
        assertEquals("value", obj.getAsJsonObject("nested").getAsString("key"));
    }

    // ============ toJsonElement: bean ============

    @Test
    @DisplayName("Bean to JsonObject")
    void testBeanToJson() {
        var bean = new TestBean();
        bean.setName("Alice");
        bean.setAge(30);
        bean.setActive(true);

        var obj = JsonMapper.toJsonElement(bean).asJsonObject();
        assertEquals("Alice", obj.getAsString("name"));
        assertEquals(30, obj.getAsInt("age"));
        assertTrue(obj.getAsBoolean("active"));
    }

    @Test
    @DisplayName("Bean with null field")
    void testBeanWithNull() {
        var bean = new TestBean();
        bean.setName("Alice");

        var obj = JsonMapper.toJsonElement(bean).asJsonObject();
        assertEquals("Alice", obj.getAsString("name"));
        assertTrue(obj.get("age").isJsonNull() || !obj.has("age"));
    }

    @Test
    @DisplayName("Enum to JsonElement")
    void testEnumToJson() {
        var p = JsonMapper.toJsonElement(TestEnum.ACTIVE).asJsonPrimitive();
        assertEquals("ACTIVE", p.getAsString());
    }

    // ============ fromJsonElement: primitives ============

    @Test
    @DisplayName("JsonElement to String")
    void testJsonToString() {
        assertEquals("hello", JsonMapper.fromJsonElement(new JsonPrimitive("hello"), String.class));
    }

    @Test
    @DisplayName("JsonElement to int")
    void testJsonToInt() {
        assertEquals(42, (int) JsonMapper.fromJsonElement(new JsonPrimitive(42), int.class));
    }

    @Test
    @DisplayName("JsonElement to long")
    void testJsonToLong() {
        assertEquals(42L, (long) JsonMapper.fromJsonElement(new JsonPrimitive(42), long.class));
    }

    @Test
    @DisplayName("JsonElement to double")
    void testJsonToDouble() {
        assertEquals(3.14, JsonMapper.fromJsonElement(new JsonPrimitive(3.14), double.class), 0.001);
    }

    @Test
    @DisplayName("JsonElement to boolean")
    void testJsonToBoolean() {
        assertTrue(JsonMapper.fromJsonElement(new JsonPrimitive(true), boolean.class));
    }

    @Test
    @DisplayName("Json null to object")
    void testJsonNullToObject() {
        assertNull(JsonMapper.fromJsonElement(JsonNull.INSTANCE, String.class));
    }

    @Test
    @DisplayName("JsonElement to Object (plain)")
    void testJsonToObject() {
        var result = JsonMapper.fromJsonElement(new JsonPrimitive("hello"), Object.class);
        assertEquals("hello", result);

        var num = JsonMapper.fromJsonElement(new JsonPrimitive(42), Object.class);
        assertInstanceOf(Number.class, num);
    }

    // ============ fromJsonElement: arrays and collections ============

    @Test
    @DisplayName("JsonArray to int array")
    void testJsonToIntArray() {
        var arr = new JsonArray();
        arr.add(1);
        arr.add(2);
        arr.add(3);

        int[] result = JsonMapper.fromJsonElement(arr, int[].class);
        assertArrayEquals(new int[]{1, 2, 3}, result);
    }

    @Test
    @DisplayName("JsonArray to String array")
    void testJsonToStringArray() {
        var arr = new JsonArray();
        arr.add("a");
        arr.add("b");

        String[] result = JsonMapper.fromJsonElement(arr, String[].class);
        assertArrayEquals(new String[]{"a", "b"}, result);
    }

    @Test
    @DisplayName("JsonArray to List<String>")
    void testJsonToList() {
        var arr = new JsonArray();
        arr.add("x");
        arr.add("y");

        List<String> result = JsonMapper.fromJsonElement(arr, List.class, String.class);
        assertEquals(List.of("x", "y"), result);
    }

    @Test
    @DisplayName("JsonArray to Set<Integer>")
    void testJsonToSet() {
        var arr = new JsonArray();
        arr.add(1);
        arr.add(2);
        arr.add(1);

        Set<Integer> result = JsonMapper.fromJsonElement(arr, Set.class, Integer.class);
        assertEquals(2, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));
    }

    // ============ fromJsonElement: maps ============

    @Test
    @DisplayName("JsonObject to Map<String, Object>")
    void testJsonToMap() {
        var obj = new JsonObject();
        obj.addProperty("name", "Alice");
        obj.addProperty("age", 30);

        Map<String, Object> result = JsonMapper.fromJsonElement(obj, Map.class, String.class, Object.class);
        assertEquals("Alice", result.get("name"));
        assertInstanceOf(Number.class, result.get("age"));
    }

    // ============ fromJsonElement: beans ============

    @Test
    @DisplayName("JsonObject to bean")
    void testJsonToBean() {
        var obj = new JsonObject();
        obj.addProperty("name", "Alice");
        obj.addProperty("age", 30);
        obj.addProperty("active", true);

        var bean = JsonMapper.fromJsonElement(obj, TestBean.class);
        assertEquals("Alice", bean.getName());
        assertEquals(30, bean.getAge());
        assertTrue(bean.isActive());
    }

    @Test
    @DisplayName("JsonObject to bean with partial fields")
    void testJsonToBeanPartial() {
        var obj = new JsonObject();
        obj.addProperty("name", "Bob");

        var bean = JsonMapper.fromJsonElement(obj, TestBean.class);
        assertEquals("Bob", bean.getName());
        assertEquals(0, bean.getAge());
    }

    // ============ round-trip ============

    @Test
    @DisplayName("Bean -> JsonElement -> Bean round-trip")
    void testBeanRoundTrip() {
        var original = new TestBean();
        original.setName("Alice");
        original.setAge(30);
        original.setActive(true);

        var element = JsonMapper.toJsonElement(original);
        var restored = JsonMapper.fromJsonElement(element, TestBean.class);

        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
        assertEquals(original.isActive(), restored.isActive());
    }

    @Test
    @DisplayName("Complex nested round-trip")
    void testComplexRoundTrip() {
        var address = new Address();
        address.setCity("Beijing");
        address.setZip("100000");

        var bean = new BeanWithAddress();
        bean.setName("Alice");
        bean.setAddress(address);
        bean.setScores(List.of(90, 85, 95));

        var element = JsonMapper.toJsonElement(bean);
        var restored = JsonMapper.fromJsonElement(element, BeanWithAddress.class);

        assertEquals("Alice", restored.getName());
        assertNotNull(restored.getAddress());
        assertEquals("Beijing", restored.getAddress().getCity());
        assertEquals("100000", restored.getAddress().getZip());
        assertEquals(List.of(90, 85, 95), restored.getScores());
    }

    @Test
    @DisplayName("Null handling in bean")
    void testBeanNullHandling() {
        var bean = new TestBean();
        bean.setName(null);
        bean.setAge(25);

        var element = JsonMapper.toJsonElement(bean);
        var restored = JsonMapper.fromJsonElement(element, TestBean.class);
        assertEquals(25, restored.getAge());
        assertNull(restored.getName());
    }

    // ============ toPlainObject ============

    @Test
    @DisplayName("ToPlainObject for JsonObject")
    void testToPlainObject() {
        var obj = new JsonObject();
        obj.addProperty("name", "Alice");
        obj.addProperty("age", 30);

        var plain = JsonMapper.toPlainObject(obj);
        assertInstanceOf(Map.class, plain);
        var map = (Map<String, Object>) plain;
        assertEquals("Alice", map.get("name"));
        assertInstanceOf(Integer.class, map.get("age"));
        assertEquals(30, map.get("age"));
    }

    @Test
    @DisplayName("ToPlainObject for JsonArray")
    void testToPlainObjectArray() {
        var arr = new JsonArray();
        arr.add(1);
        arr.add(2);
        arr.add(3);

        var plain = JsonMapper.toPlainObject(arr);
        assertInstanceOf(List.class, plain);
        assertEquals(List.of(1, 2, 3), plain);
    }

    @Test
    @DisplayName("ToPlainObject for null")
    void testToPlainObjectNull() {
        assertNull(JsonMapper.toPlainObject(JsonNull.INSTANCE));
        assertNull(JsonMapper.toPlainObject(null));
    }

    // ============ JsonUtils integration ============

    @Test
    @DisplayName("JsonUtils fromJson / toJson round-trip")
    void testJsonUtilsRoundTrip() {
        var bean = new TestBean();
        bean.setName("Alice");
        bean.setAge(30);

        String json = JsonUtils.toJson(bean);
        var restored = JsonUtils.fromJson(json, TestBean.class);

        assertEquals("Alice", restored.getName());
        assertEquals(30, restored.getAge());
    }

    @Test
    @DisplayName("JsonUtils parse and serialize")
    void testJsonUtilsParseAndSerialize() {
        var json = "{\"key\":\"value\"}";
        var element = JsonUtils.parse(json);
        var serialized = JsonUtils.serialize(element);
        assertEquals(element, JsonUtils.parse(serialized));
    }

    @Test
    @DisplayName("JsonUtils prettyPrint")
    void testJsonUtilsPrettyPrint() {
        var obj = new JsonObject();
        obj.addProperty("a", "1");
        var pretty = JsonUtils.prettyPrint(obj);
        assertTrue(pretty.contains("\n"));
    }

    // ============ test beans ============

    public static class TestBean {
        private String name;
        private int age;
        private boolean active;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    public static class BeanWithAddress {
        private String name;
        private Address address;
        private List<Integer> scores;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
        public List<Integer> getScores() { return scores; }
        public void setScores(List<Integer> scores) { this.scores = scores; }
    }

    public static class Address {
        private String city;
        private String zip;

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
    }

    public enum TestEnum {
        ACTIVE, INACTIVE
    }
}