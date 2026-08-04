/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.yaml;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Converts between {@link YamlNode} trees and Java objects using reflection.
 * <p>
 * Supports:
 * <ul>
 *   <li>Primitive types and their wrappers</li>
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
public class YamlMapper {

    /**
     * Converts a Java object to a {@link YamlNode}.
     *
     * @param obj the Java object to convert
     * @return the corresponding YamlNode
     */
    public static YamlNode toYamlNode(Object obj) {
        if (obj == null) {
            return YamlNull.INSTANCE;
        }
        if (obj instanceof YamlNode) {
            return (YamlNode) obj;
        }
        if (obj instanceof String) {
            return new YamlScalar((String) obj);
        }
        if (obj instanceof Boolean) {
            return new YamlScalar((Boolean) obj);
        }
        if (obj instanceof Number) {
            return new YamlScalar((Number) obj);
        }
        if (obj instanceof Character) {
            return new YamlScalar(obj.toString());
        }
        if (obj instanceof Enum) {
            return new YamlScalar(((Enum<?>) obj).name());
        }
        if (obj instanceof Map) {
            return mapToYamlMapping((Map<?, ?>) obj);
        }
        if (obj instanceof Collection) {
            return collectionToYamlSequence((Collection<?>) obj);
        }
        if (obj.getClass().isArray()) {
            return arrayToYamlSequence(obj);
        }
        return beanToYamlMapping(obj);
    }

    /**
     * Converts a {@link YamlNode} to a Java object of the specified type.
     *
     * @param <T>   the target type
     * @param node  the YAML node
     * @param clazz the target class
     * @return the converted Java object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromYamlNode(YamlNode node, Class<T> clazz) {
        if (node == null || node.isNull()) {
            return null;
        }
        Object result = convertToType(node, clazz, null);
        return (T) result;
    }

    /**
     * Converts a {@link YamlNode} to a Java object with generic type parameters.
     *
     * @param <T>              the raw target type
     * @param node             the YAML node
     * @param rawType          the raw class
     * @param typeArguments    the type arguments
     * @return the converted Java object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromYamlNode(YamlNode node, Class<T> rawType, Class<?>... typeArguments) {
        if (node == null || node.isNull()) {
            return null;
        }
        Object result = convertToType(node, rawType, typeArguments);
        return (T) result;
    }

    // ============ toYamlNode internals ============

    private static YamlMapping mapToYamlMapping(Map<?, ?> map) {
        YamlMapping result = new YamlMapping(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey().toString() : "null";
            result.add(key, toYamlNode(entry.getValue()));
        }
        return result;
    }

    private static YamlSequence collectionToYamlSequence(Collection<?> collection) {
        YamlSequence result = new YamlSequence(collection.size());
        for (Object item : collection) {
            result.add(toYamlNode(item));
        }
        return result;
    }

    private static YamlSequence arrayToYamlSequence(Object array) {
        int length = Array.getLength(array);
        YamlSequence result = new YamlSequence(length);
        for (int i = 0; i < length; i++) {
            result.add(toYamlNode(Array.get(array, i)));
        }
        return result;
    }

    private static YamlMapping beanToYamlMapping(Object bean) {
        YamlMapping result = new YamlMapping();
        Class<?> clazz = bean.getClass();
        // public fields
        for (Field field : clazz.getFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                continue;
            }
            try {
                result.add(field.getName(), toYamlNode(field.get(bean)));
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
            if (name.startsWith("get") && name.length() > 3) {
                propertyName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            } else if (name.startsWith("is") && name.length() > 2 &&
                    (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
                propertyName = Character.toLowerCase(name.charAt(2)) + name.substring(3);
            }
            if (propertyName != null && !"class".equals(propertyName) && !processed.contains(propertyName)) {
                try {
                    clazz.getField(propertyName);
                    continue;
                } catch (NoSuchFieldException e) {
                    // not a public field, process via getter
                }
                try {
                    method.setAccessible(true);
                    Object value = method.invoke(bean);
                    result.add(propertyName, toYamlNode(value));
                    processed.add(propertyName);
                } catch (Exception e) {
                    // skip
                }
            }
        }
        return result;
    }

    // ============ fromYamlNode internals ============

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object convertToType(YamlNode node, Class<?> clazz, Class<?>[] typeArgs) {
        if (node == null || node.isNull()) {
            return null;
        }

        // Primitive and wrapper types
        if (clazz == String.class) {
            return node.isScalar() ? node.asScalar().getAsString() : node.toString();
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            if (node.isScalar()) {
                return node.asScalar().getAsBoolean();
            }
            return Boolean.parseBoolean(node.toString());
        }
        if (clazz == int.class || clazz == Integer.class) {
            return node.isScalar() ? node.asScalar().getAsInt() : 0;
        }
        if (clazz == long.class || clazz == Long.class) {
            return node.isScalar() ? node.asScalar().getAsLong() : 0L;
        }
        if (clazz == double.class || clazz == Double.class) {
            return node.isScalar() ? node.asScalar().getAsDouble() : 0.0;
        }
        if (clazz == float.class || clazz == Float.class) {
            return node.isScalar() ? (float) node.asScalar().getAsDouble() : 0.0f;
        }
        if (clazz == short.class || clazz == Short.class) {
            return node.isScalar() ? (short) node.asScalar().getAsInt() : (short) 0;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return node.isScalar() ? (byte) node.asScalar().getAsInt() : (byte) 0;
        }
        if (clazz == char.class || clazz == Character.class) {
            String s = node.isScalar() ? node.asScalar().getAsString() : node.toString();
            return s.isEmpty() ? '\0' : s.charAt(0);
        }
        if (clazz == BigDecimal.class) {
            return node.isScalar() ? node.asScalar().getAsBigDecimal() : BigDecimal.ZERO;
        }
        if (clazz == BigInteger.class) {
            return node.isScalar() ? node.asScalar().getAsBigInteger() : BigInteger.ZERO;
        }
        if (clazz == Number.class) {
            if (node.isScalar()) {
                return node.asScalar().getAsNumber();
            }
            return 0;
        }
        if (clazz == Object.class) {
            return toPlainObject(node);
        }

        // Enum
        if (clazz.isEnum()) {
            String name = node.isScalar() ? node.asScalar().getAsString() : node.toString();
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
            return yamlSequenceToArray(node, clazz);
        }

        // Collection
        if (Collection.class.isAssignableFrom(clazz)) {
            return yamlSequenceToCollection(node, clazz, typeArgs != null && typeArgs.length > 0 ? typeArgs[0] : Object.class);
        }

        // Map
        if (Map.class.isAssignableFrom(clazz)) {
            return yamlMappingToMap(node, clazz, typeArgs);
        }

        // Custom bean
        if (node.isMapping()) {
            return yamlMappingToBean(node.asMapping(), clazz);
        }

        return null;
    }

    /**
     * Converts a YamlNode to a plain Java object (Map, List, String, Number, Boolean, null).
     */
    public static Object toPlainObject(YamlNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isMapping()) {
            YamlMapping mapping = node.asMapping();
            Map<String, Object> map = new LinkedHashMap<>(mapping.size());
            for (Map.Entry<String, YamlNode> entry : mapping) {
                map.put(entry.getKey(), toPlainObject(entry.getValue()));
            }
            return map;
        }
        if (node.isSequence()) {
            YamlSequence seq = node.asSequence();
            List<Object> list = new ArrayList<>(seq.size());
            for (YamlNode n : seq) {
                list.add(toPlainObject(n));
            }
            return list;
        }
        if (node.isScalar()) {
            YamlScalar s = node.asScalar();
            if (s.isString()) return s.getAsString();
            if (s.isBoolean()) return s.getAsBoolean();
            if (s.isNumber()) return s.getAsNumber();
        }
        return null;
    }

    // ============ array conversion ============

    private static Object yamlSequenceToArray(YamlNode node, Class<?> arrayType) {
        if (!node.isSequence()) {
            return Array.newInstance(arrayType.getComponentType(), 0);
        }
        YamlSequence sequence = node.asSequence();
        Class<?> componentType = arrayType.getComponentType();
        Object result = Array.newInstance(componentType, sequence.size());
        for (int i = 0; i < sequence.size(); i++) {
            Array.set(result, i, convertToType(sequence.get(i), componentType, null));
        }
        return result;
    }

    // ============ collection conversion ============

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Collection yamlSequenceToCollection(YamlNode node, Class<?> collectionType, Class<?> elementType) {
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
        if (node.isSequence()) {
            YamlSequence sequence = node.asSequence();
            for (YamlNode item : sequence) {
                result.add(convertToType(item, elementType, null));
            }
        }
        return result;
    }

    // ============ map conversion ============

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map yamlMappingToMap(YamlNode node, Class<?> mapType, Class<?>[] typeArgs) {
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
        if (node.isMapping()) {
            Class<?> valueType = (typeArgs != null && typeArgs.length > 1) ? typeArgs[1] : Object.class;
            YamlMapping mapping = node.asMapping();
            for (Map.Entry<String, YamlNode> entry : mapping) {
                result.put(entry.getKey(), convertToType(entry.getValue(), valueType, null));
            }
        }
        return result;
    }

    // ============ bean conversion ============

    @SuppressWarnings("unchecked")
    private static <T> T yamlMappingToBean(YamlMapping mapping, Class<T> beanClass) {
        T bean;
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            return yamlMappingToBeanWithConstructor(mapping, beanClass);
        } catch (Exception e) {
            throw new YamlException("Cannot create instance of " + beanClass.getName(), e);
        }

        // set public fields
        for (Field field : beanClass.getFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                continue;
            }
            YamlNode value = mapping.get(field.getName());
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
            try {
                Field field = beanClass.getField(property);
                continue;
            } catch (NoSuchFieldException e) {
                // not a public field, proceed with setter
            }
            YamlNode yamlValue = mapping.get(property);
            if (yamlValue != null) {
                Method setter = entry.getValue();
                Class<?> paramType = setter.getParameterTypes()[0];
                try {
                    setter.setAccessible(true);
                    setter.invoke(bean, convertToType(yamlValue, paramType, null));
                } catch (Exception e) {
                    // skip
                }
            }
        }

        return bean;
    }

    @SuppressWarnings("unchecked")
    private static <T> T yamlMappingToBeanWithConstructor(YamlMapping mapping, Class<T> beanClass) {
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
                YamlNode value = mapping.get(paramNames[i]);
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
                    throw new YamlException("Cannot create instance of " + beanClass.getName() + " with constructor", e);
                }
            }
        }
        throw new YamlException("Cannot create instance of " + beanClass.getName() +
                ": no suitable constructor found");
    }

    private static String[] getParameterNames(Constructor<?> constructor) {
        String[] names = new String[constructor.getParameterCount()];
        Parameter[] parameters = constructor.getParameters();
        boolean hasRealNames = true;
        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].getName();
            if (name.startsWith("arg")) {
                hasRealNames = false;
                break;
            }
            names[i] = name;
        }
        if (!hasRealNames) {
            return null;
        }
        return names;
    }
}