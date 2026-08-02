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

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Converts between {@link JsonElement} trees and Java objects using reflection.
 * <p>
 * Supports:
 * <ul>
 *   <li>Primitive types and their wrappers (int, Integer, long, Long, double, Double, boolean, Boolean, etc.)</li>
 *   <li>String, BigDecimal, BigInteger</li>
 *   <li>Arrays (primitive and object arrays)</li>
 *   <li>Collections (List, Set, and their implementations)</li>
 *   <li>Maps (Map&lt;String, ?&gt;)</li>
 *   <li>Custom Java beans (using getter/setter conventions or public fields)</li>
 *   <li>Enum types</li>
 *   <li>Nested objects and generic types</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class JsonMapper {

    /**
     * Converts a Java object to a {@link JsonElement}.
     *
     * @param obj the Java object to convert
     * @return the corresponding JsonElement
     */
    public static JsonElement toJsonElement(Object obj) {
        if (obj == null) {
            return JsonNull.INSTANCE;
        }
        if (obj instanceof JsonElement) {
            return (JsonElement) obj;
        }
        if (obj instanceof String) {
            return new JsonPrimitive((String) obj);
        }
        if (obj instanceof Boolean) {
            return new JsonPrimitive((Boolean) obj);
        }
        if (obj instanceof Number) {
            return new JsonPrimitive((Number) obj);
        }
        if (obj instanceof Character) {
            return new JsonPrimitive(obj.toString());
        }
        if (obj instanceof Enum) {
            return new JsonPrimitive(((Enum<?>) obj).name());
        }
        if (obj instanceof Map) {
            return mapToJsonObject((Map<?, ?>) obj);
        }
        if (obj instanceof Collection) {
            return collectionToJsonArray((Collection<?>) obj);
        }
        if (obj.getClass().isArray()) {
            return arrayToJsonArray(obj);
        }
        return beanToJsonObject(obj);
    }

    /**
     * Converts a {@link JsonElement} to a Java object of the specified type.
     *
     * @param <T>   the target type
     * @param element the JSON element
     * @param clazz   the target class
     * @return the converted Java object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJsonElement(JsonElement element, Class<T> clazz) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        Object result = convertToType(element, clazz, null);
        return (T) result;
    }

    /**
     * Converts a {@link JsonElement} to a Java object with generic type parameters.
     * <p>
     * For example, to convert to {@code List<String>}, call:
     * {@code fromJsonElement(element, List.class, String.class)}
     *
     * @param <T>              the raw target type
     * @param element          the JSON element
     * @param rawType          the raw class
     * @param typeArguments    the type arguments
     * @return the converted Java object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJsonElement(JsonElement element, Class<T> rawType, Class<?>... typeArguments) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        Object result = convertToType(element, rawType, typeArguments);
        return (T) result;
    }

    // ============ toJsonElement internals ============

    private static JsonObject mapToJsonObject(Map<?, ?> map) {
        JsonObject result = new JsonObject(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey().toString() : "null";
            result.add(key, toJsonElement(entry.getValue()));
        }
        return result;
    }

    private static JsonArray collectionToJsonArray(Collection<?> collection) {
        JsonArray result = new JsonArray(collection.size());
        for (Object item : collection) {
            result.add(toJsonElement(item));
        }
        return result;
    }

    private static JsonArray arrayToJsonArray(Object array) {
        int length = Array.getLength(array);
        JsonArray result = new JsonArray(length);
        for (int i = 0; i < length; i++) {
            result.add(toJsonElement(Array.get(array, i)));
        }
        return result;
    }

    private static JsonObject beanToJsonObject(Object bean) {
        JsonObject result = new JsonObject();
        Class<?> clazz = bean.getClass();
        // public fields
        for (Field field : clazz.getFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                continue;
            }
            try {
                result.add(field.getName(), toJsonElement(field.get(bean)));
            } catch (IllegalAccessException e) {
                // skip
            }
        }
        // getter methods
        Set<String> processed = new HashSet<>();
        for (Method method : clazz.getMethods()) {
            int mod = method.getModifiers();
            if (Modifier.isStatic(mod)) {
                continue;
            }
            String name = method.getName();
            if (method.getParameterCount() != 0 || method.getReturnType() == void.class) {
                continue;
            }
            String propertyName = null;
            if (name.startsWith("get") && name.length() > 3 && name.charAt(3) == name.charAt(3)) {
                propertyName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            } else if (name.startsWith("is") && name.length() > 2 &&
                    (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
                propertyName = Character.toLowerCase(name.charAt(2)) + name.substring(3);
            }
            if (propertyName != null && !"class".equals(propertyName) && !processed.contains(propertyName)) {
                // skip if already handled by a public field
                try {
                    clazz.getField(propertyName);
                    continue;
                } catch (NoSuchFieldException e) {
                    // not a public field, process via getter
                }
                try {
                    method.setAccessible(true);
                    Object value = method.invoke(bean);
                    result.add(propertyName, toJsonElement(value));
                    processed.add(propertyName);
                } catch (Exception e) {
                    // skip
                }
            }
        }
        return result;
    }

    // ============ fromJsonElement internals ============

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object convertToType(JsonElement element, Class<?> clazz, Class<?>[] typeArgs) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        // Primitive and wrapper types
        if (clazz == String.class) {
            return element.isJsonPrimitive() ? element.asJsonPrimitive().getAsString() : element.toString();
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            if (element.isJsonPrimitive()) {
                return element.asJsonPrimitive().getAsBoolean();
            }
            return Boolean.parseBoolean(element.toString());
        }
        if (clazz == int.class || clazz == Integer.class) {
            return element.isJsonPrimitive() ? element.asJsonPrimitive().getAsInt() : 0;
        }
        if (clazz == long.class || clazz == Long.class) {
            return element.isJsonPrimitive() ? element.asJsonPrimitive().getAsLong() : 0L;
        }
        if (clazz == double.class || clazz == Double.class) {
            return element.isJsonPrimitive() ? element.asJsonPrimitive().getAsDouble() : 0.0;
        }
        if (clazz == float.class || clazz == Float.class) {
            return element.isJsonPrimitive() ? (float) element.asJsonPrimitive().getAsDouble() : 0.0f;
        }
        if (clazz == short.class || clazz == Short.class) {
            return element.isJsonPrimitive() ? (short) element.asJsonPrimitive().getAsInt() : (short) 0;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return element.isJsonPrimitive() ? (byte) element.asJsonPrimitive().getAsInt() : (byte) 0;
        }
        if (clazz == char.class || clazz == Character.class) {
            String s = element.isJsonPrimitive() ? element.asJsonPrimitive().getAsString() : element.toString();
            return s.isEmpty() ? '\0' : s.charAt(0);
        }
        if (clazz == BigDecimal.class) {
            return element.isJsonPrimitive() ? element.asJsonPrimitive().getAsBigDecimal() : BigDecimal.ZERO;
        }
        if (clazz == BigInteger.class) {
            return element.isJsonPrimitive() ? element.asJsonPrimitive().getAsBigInteger() : BigInteger.ZERO;
        }
        if (clazz == Number.class) {
            if (element.isJsonPrimitive()) {
                return element.asJsonPrimitive().getAsNumber();
            }
            return 0;
        }
        if (clazz == Object.class) {
            return toPlainObject(element);
        }

        // Enum
        if (clazz.isEnum()) {
            String name = element.isJsonPrimitive() ? element.asJsonPrimitive().getAsString() : element.toString();
            for (Object constant : clazz.getEnumConstants()) {
                if (((Enum<?>) constant).name().equals(name)) {
                    return constant;
                }
            }
            // fallback: try case-insensitive
            for (Object constant : clazz.getEnumConstants()) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(name)) {
                    return constant;
                }
            }
            return null;
        }

        // Array
        if (clazz.isArray()) {
            return jsonArrayToArray(element, clazz);
        }

        // Collection
        if (Collection.class.isAssignableFrom(clazz)) {
            return jsonArrayToCollection(element, clazz, typeArgs != null && typeArgs.length > 0 ? typeArgs[0] : Object.class);
        }

        // Map
        if (Map.class.isAssignableFrom(clazz)) {
            return jsonObjectToMap(element, clazz, typeArgs);
        }

        // Custom bean
        if (element.isJsonObject()) {
            return jsonObjectToBean(element.asJsonObject(), clazz);
        }

        return null;
    }

    /**
     * Converts a JsonElement to a plain Java object (Map, List, String, Number, Boolean, null).
     */
    public static Object toPlainObject(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonObject()) {
            JsonObject obj = element.asJsonObject();
            Map<String, Object> map = new LinkedHashMap<>(obj.size());
            for (Map.Entry<String, JsonElement> entry : obj) {
                map.put(entry.getKey(), toPlainObject(entry.getValue()));
            }
            return map;
        }
        if (element.isJsonArray()) {
            JsonArray arr = element.asJsonArray();
            List<Object> list = new ArrayList<>(arr.size());
            for (JsonElement e : arr) {
                list.add(toPlainObject(e));
            }
            return list;
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive p = element.asJsonPrimitive();
            if (p.isString()) return p.getAsString();
            if (p.isBoolean()) return p.getAsBoolean();
            if (p.isNumber()) return p.getAsNumber();
        }
        return null;
    }

    // ============ array conversion ============

    private static Object jsonArrayToArray(JsonElement element, Class<?> arrayType) {
        if (!element.isJsonArray()) {
            return Array.newInstance(arrayType.getComponentType(), 0);
        }
        JsonArray jsonArray = element.asJsonArray();
        Class<?> componentType = arrayType.getComponentType();
        Object result = Array.newInstance(componentType, jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            Array.set(result, i, convertToType(jsonArray.get(i), componentType, null));
        }
        return result;
    }

    // ============ collection conversion ============

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Collection jsonArrayToCollection(JsonElement element, Class<?> collectionType, Class<?> elementType) {
        Collection result;
        if (collectionType.isInterface()) {
            if (List.class.isAssignableFrom(collectionType)) {
                result = new ArrayList<>();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                result = new LinkedHashSet<>();
            } else {
                result = new ArrayList<>();
            }
        } else {
            try {
                result = (Collection) collectionType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                result = new ArrayList<>();
            }
        }
        if (element.isJsonArray()) {
            JsonArray jsonArray = element.asJsonArray();
            for (JsonElement item : jsonArray) {
                result.add(convertToType(item, elementType, null));
            }
        }
        return result;
    }

    // ============ map conversion ============

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map jsonObjectToMap(JsonElement element, Class<?> mapType, Class<?>[] typeArgs) {
        Map result;
        if (mapType.isInterface()) {
            result = new LinkedHashMap<>();
        } else {
            try {
                result = (Map) mapType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                result = new LinkedHashMap<>();
            }
        }
        if (element.isJsonObject()) {
            Class<?> valueType = (typeArgs != null && typeArgs.length > 1) ? typeArgs[1] : Object.class;
            JsonObject obj = element.asJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj) {
                result.put(entry.getKey(), convertToType(entry.getValue(), valueType, null));
            }
        }
        return result;
    }

    // ============ bean conversion ============

    @SuppressWarnings("unchecked")
    private static <T> T jsonObjectToBean(JsonObject jsonObject, Class<T> beanClass) {
        T bean;
        try {
            // first try no-arg constructor
            bean = beanClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            // try to match constructor parameters with JSON keys
            return jsonObjectToBeanWithConstructor(jsonObject, beanClass);
        } catch (Exception e) {
            throw new JsonException("Cannot create instance of " + beanClass.getName(), e);
        }

        // set public fields
        for (Field field : beanClass.getFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                continue;
            }
            JsonElement value = jsonObject.get(field.getName());
            if (value != null) {
                try {
                    field.set(bean, convertToType(value, field.getType(), null));
                } catch (Exception e) {
                    // skip
                }
            }
        }

        // set via setter methods
        Map<String, Method> setters = new HashMap<>();
        for (Method method : beanClass.getMethods()) {
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3 && method.getParameterCount() == 1) {
                String property = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                setters.put(property, method);
            }
        }

        for (Map.Entry<String, Method> entry : setters.entrySet()) {
            String property = entry.getKey();
            // skip if already set via public field
            try {
                Field field = beanClass.getField(property);
                continue;
            } catch (NoSuchFieldException e) {
                // not a public field, proceed with setter
            }
            JsonElement jsonValue = jsonObject.get(property);
            if (jsonValue != null) {
                Method setter = entry.getValue();
                Class<?> paramType = setter.getParameterTypes()[0];
                try {
                    setter.setAccessible(true);
                    setter.invoke(bean, convertToType(jsonValue, paramType, null));
                } catch (Exception e) {
                    // skip
                }
            }
        }

        return bean;
    }

    @SuppressWarnings("unchecked")
    private static <T> T jsonObjectToBeanWithConstructor(JsonObject jsonObject, Class<T> beanClass) {
        // try to find a constructor whose parameter names match JSON keys
        Constructor<?>[] constructors = beanClass.getConstructors();
        Arrays.sort(constructors, (a, b) -> a.getParameterCount() - b.getParameterCount());

        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                continue;
            }
            Class<?>[] paramTypes = constructor.getParameterTypes();
            String[] paramNames = getParameterNames(constructor);
            if (paramNames == null) {
                continue;
            }
            boolean allMatch = true;
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                JsonElement value = jsonObject.get(paramNames[i]);
                if (value == null) {
                    allMatch = false;
                    break;
                }
                args[i] = convertToType(value, paramTypes[i], null);
            }
            if (allMatch) {
                try {
                    return (T) constructor.newInstance(args);
                } catch (Exception e) {
                    throw new JsonException("Cannot create instance of " + beanClass.getName() + " with constructor", e);
                }
            }
        }
        throw new JsonException("Cannot create instance of " + beanClass.getName() +
                ": no suitable constructor found (need a no-arg constructor or constructor with parameters matching JSON keys)");
    }

    /**
     * Attempts to get parameter names from a constructor.
     * Uses the "-parameters" compiler flag if available, otherwise generates names.
     */
    private static String[] getParameterNames(Constructor<?> constructor) {
        String[] names = new String[constructor.getParameterCount()];
        // Try to get parameter names via reflection (requires -parameters compiler flag)
        Parameter[] parameters = constructor.getParameters();
        boolean hasRealNames = true;
        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].getName();
            // Synthetic names like "arg0" indicate the -parameters flag was not used
            if (name.startsWith("arg")) {
                hasRealNames = false;
                break;
            }
            names[i] = name;
        }
        if (!hasRealNames) {
            // Without -parameters, we cannot reliably match constructor parameters
            return null;
        }
        return names;
    }
}