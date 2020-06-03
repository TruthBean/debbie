/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import com.truthbean.debbie.util.Constants;
import com.truthbean.debbie.util.NumericUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-09 14:37
 */
public final class TypeHelper {
    private TypeHelper() {
    }

    private static final Map<String, Class<?>> CLASS_MAP = new HashMap<>();

    private static final Map<Class<?>, Class<?>> BASE_TYPE_MAP = new HashMap<>();

    static {
        CLASS_MAP.put(Integer.class.getName(), Integer.class);
        CLASS_MAP.put(int.class.getName(), int.class);
        CLASS_MAP.put(Short.class.getName(), Short.class);
        CLASS_MAP.put(short.class.getName(), short.class);
        CLASS_MAP.put(long.class.getName(), long.class);
        CLASS_MAP.put(Long.class.getName(), Long.class);
        CLASS_MAP.put(float.class.getName(), Float.class);
        CLASS_MAP.put(Float.class.getName(), float.class);
        CLASS_MAP.put(Double.class.getName(), Double.class);
        CLASS_MAP.put(double.class.getName(), double.class);
        CLASS_MAP.put(Boolean.class.getName(), Boolean.class);
        CLASS_MAP.put(boolean.class.getName(), boolean.class);
        CLASS_MAP.put(Character.class.getName(), Character.class);
        CLASS_MAP.put(char.class.getName(), char.class);
        CLASS_MAP.put(Byte.class.getName(), Byte.class);
        CLASS_MAP.put(byte.class.getName(), byte.class);
        CLASS_MAP.put(Object.class.getName(), Object.class);

        BASE_TYPE_MAP.put(int.class, Integer.class);
        BASE_TYPE_MAP.put(short.class, Short.class);
        BASE_TYPE_MAP.put(long.class, Long.class);
        BASE_TYPE_MAP.put(float.class, Float.class);
        BASE_TYPE_MAP.put(double.class, Double.class);
        BASE_TYPE_MAP.put(boolean.class, Boolean.class);
        BASE_TYPE_MAP.put(char.class, Character.class);
        BASE_TYPE_MAP.put(byte.class, Byte.class);
    }

    public static Class<?> getWrapperClass(Class<?> baseType) {
        for (Map.Entry<Class<?>, Class<?>> classClassEntry : BASE_TYPE_MAP.entrySet()) {
            if (classClassEntry.getKey() == baseType) {
                return classClassEntry.getValue();
            }
        }
        return null;
    }

    public static Class<?> getClass(String className) {
        return CLASS_MAP.get(className);
    }

    public static boolean isInt(Class<?> clazz) {
        return clazz == Integer.class || clazz == int.class;
    }

    public static boolean isShort(Class<?> clazz) {
        return clazz == Short.class || clazz == short.class;
    }

    public static boolean isLong(Class<?> clazz) {
        return clazz == Long.class || clazz == long.class;
    }

    public static boolean isFloat(Class<?> clazz) {
        return clazz == float.class || clazz == Float.class;
    }

    public static boolean isDouble(Class<?> clazz) {
        return clazz == Double.class || clazz == double.class;
    }

    public static boolean isBoolean(Class<?> clazz) {
        return clazz == Boolean.class || clazz == boolean.class;
    }

    public static boolean isChar(Class<?> clazz) {
        return clazz == Character.class || clazz == char.class;
    }

    public static boolean isByte(Class<?> clazz) {
        return clazz == Byte.class || clazz == byte.class;
    }

    public static boolean isObject(Class<?> clazz) {
        return clazz == Object.class;
    }

    public static boolean isBaseType(java.lang.reflect.Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return isBoolean(clazz) || isDouble(clazz) || isFloat(clazz) || isInt(clazz) || isLong(clazz) || isShort(clazz) || isChar(clazz) || isByte(clazz) || clazz == String.class || isObject(clazz);
        }
        return false;
    }

    public static boolean isRawBaseType(Class<?> clazz) {
        return boolean.class == clazz || double.class == clazz || float.class == clazz || int.class == clazz || long.class == clazz || short.class == clazz || char.class == clazz || byte.class == clazz;
    }

    public static boolean isSameType(Class<?> aType, Class<?> bType) {
        return (isInt(aType) && isInt(bType)
                || (isShort(aType) && isShort(bType))
                || (isLong(aType) && isLong(bType))
                || (isFloat(aType)) && isFloat(bType)
                || (isDouble(aType) && isDouble(bType))
                || (isBoolean(aType) && isBoolean(bType))
                || (isChar(aType) && isChar(bType))
                || (isByte(aType)) && isByte(bType)
                || (aType == bType));
    }

    public static boolean isArrayType(Class<?> clazz) {
        return clazz == Set.class || clazz == Map.class || clazz == List.class;
    }

    public static boolean isArrayType(java.lang.reflect.Type type) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            return clazz == Set.class || clazz == Map.class || clazz == List.class;
        } else if (type instanceof ParameterizedType) {
            java.lang.reflect.Type rawType = ((ParameterizedType) type).getRawType();
            return rawType == Set.class || rawType == Map.class || rawType == List.class;
        }
        return false;
    }

    public static boolean isAbstractOrInterface(Class<?> type) {
        return Modifier.isAbstract(type.getModifiers()) || type.isInterface();
    }

    public static boolean hasDefaultConstructor(Class<?> type) {
        Constructor<?>[] constructors = type.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    public static int getAccessByModifiers(int modifier) {
        if (Modifier.isPublic(modifier)) {
            return Opcodes.ACC_PUBLIC;
        } else if (Modifier.isProtected(modifier)) {
            return  Opcodes.ACC_PROTECTED;
        } else if (Modifier.isPrivate(modifier)) {
            return  Opcodes.ACC_PRIVATE;
        } else if (Modifier.isStatic(modifier)) {
            return  Opcodes.ACC_STATIC;
        } else if (Modifier.isFinal(modifier)) {
            return  Opcodes.ACC_FINAL;
        } else if (Modifier.isSynchronized(modifier)) {
            return Opcodes.ACC_SYNCHRONIZED;
        }
        return 0;
    }

    public static String[] getExceptions(Class<?>[] exceptionTypes) {
        if (exceptionTypes != null && exceptionTypes.length > 0) {
            String[] exceptions = new String[exceptionTypes.length];
            for (int i = 0; i < exceptionTypes.length; i++) {
                Class<?> exceptionType = exceptionTypes[i];
                exceptions[i] = exceptionType.getName().replace(".", "/");
            }
            return exceptions;
        }
        return null;
    }

    public static boolean isOrValueOf(Class<?> clazz, String target) {
        if (target.getClass().equals(clazz)) {
            return true;
        } else {
            if (NumericUtils.isInteger(target)) {
                return clazz == Integer.class || clazz == int.class || clazz == Short.class || clazz == short.class || clazz == Long.class || clazz == long.class;
            }

            if (NumericUtils.isDecimal(target)) {
                return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
            }

            return (Constants.TRUE.equalsIgnoreCase(target.trim()) || Constants.FALSE.equalsIgnoreCase(target.trim())) && (clazz == Boolean.class || clazz == boolean.class);

        }
    }

    public static Object valueOf(java.lang.reflect.Type type, Object value) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            java.lang.reflect.Type rawType = parameterizedType.getRawType();
            java.lang.reflect.Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (rawType == List.class) {
                List result = new ArrayList<>();
                java.lang.reflect.Type typeArgument = actualTypeArguments[0];
                if (value == null)
                    return result;
                if (value instanceof List) {
                    for (Object object : (List) value) {
                        if (object instanceof String) {
                            String str = (String) object;
                            // todo 需要改良这里的硬编码
                            if (str.contains(",")) {
                                String[] split = str.split(",");
                                if (split != null && split.length > 0) {
                                    for (String s : split) {
                                        Object o = valueOf((Class<?>) typeArgument, s);
                                        if (o != null)
                                            result.add(o);
                                    }
                                }
                            } else if (str.contains(";")) {
                                String[] split = str.split(";");
                                if (split != null && split.length > 0) {
                                    for (String s : split) {
                                        Object o = valueOf((Class<?>) typeArgument, s);
                                        if (o != null)
                                            result.add(o);
                                    }
                                }
                            } else {
                                Object o = valueOf((Class<?>) typeArgument, str);
                                if (o != null)
                                    result.add(o);
                            }
                        } else if (object.getClass() == typeArgument) {
                            result.add(((Class)typeArgument).cast(object));
                        } else if (isOrValueOf((Class)typeArgument, value)) {
                            result.add(valueOf(((Class)typeArgument), object));
                        } else {
                            result.add(((Class)typeArgument).cast(object));
                        }
                    }
                } else if (typeArgument instanceof Class) {
                    // todo transform
                    if (value instanceof String) {
                        String str = (String) value;
                        // todo 需要改良这里的硬编码
                        if (str.contains(",")) {
                            String[] split = str.split(",");
                            if (split != null && split.length > 0) {
                                for (String s : split) {
                                    Object o = valueOf((Class<?>) typeArgument, s);
                                    if (o != null)
                                        result.add(o);
                                }
                            }
                        } else if (str.contains(";")) {
                            String[] split = str.split(";");
                            if (split != null && split.length > 0) {
                                for (String s : split) {
                                    Object o = valueOf((Class<?>) typeArgument, s);
                                    if (o != null)
                                        result.add(o);
                                }
                            }
                        } else {
                            Object o = valueOf((Class<?>) typeArgument, str);
                            if (o != null)
                                result.add(o);
                        }
                    }
                }
                return result;
            }
        } else if (type instanceof Class) {
            if (value instanceof List) {
                return valueOf((Class) type, (List) value);
            } else if (isOrValueOf((Class)type, value)) {
                return valueOf(((Class)type), value);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> valueOf(Class<T> clazz, @SuppressWarnings("rawtypes") List values) {
        if (clazz == List.class) {
            java.lang.reflect.Type[] valueActualTypes = ReflectionHelper.getActualTypes(values.getClass());
            java.lang.reflect.Type[] typeActualTypes = ReflectionHelper.getActualTypes(clazz);
            if (valueActualTypes[0] == typeActualTypes[0]) {
                return (List<T>) values;
            } else {
                return null;
            }
        }
        List<T> result = new ArrayList<>();
        for (Object object : values) {
            if (object.getClass() == clazz) {
                result.add(clazz.cast(object));
            } else if (object instanceof String) {
                result.add(valueOf(clazz, (String) object));
            } else {
                result.add(clazz.cast(object));
            }
        }
        return result;
    }

    public static <T> T valueOf(Class<T> clazz, Object value) {
        if (value.getClass() == clazz) {
            return clazz.cast(value);
        } else if (value instanceof String) {
            return valueOf(clazz, (String) value);
        }

        return clazz.cast(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T valueOf(Class<T> clazz, String value) {
        if (clazz == Integer.class || clazz == int.class) {
            try {
                return (T) Integer.valueOf(value);
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (clazz == Short.class || clazz == short.class) {
            try {
                return (T) Short.valueOf(value);
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (clazz == Long.class || long.class == clazz) {
            try {
                return (T) Long.valueOf(value);
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (clazz == Float.class || clazz == float.class) {
            try {
                return (T) Float.valueOf(value);
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (clazz == Double.class || double.class == clazz) {
            try {
                return (T) Double.valueOf(value);
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (clazz == Boolean.class || clazz == boolean.class) {
            try {
                return (T) Boolean.valueOf(value);
            } catch (Exception e) {
                logger.error("", e);
                return null;
            }
        }

        if (clazz == Byte.class || clazz == byte.class) {
            try {
                return (T) Byte.valueOf(value);
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        var isChar = (clazz == Character.class || clazz == char.class) && value.length() == 1;
        if (isChar) {
            try {
                return (T) Character.valueOf(value.charAt(0));
            } catch (Exception e) {
                logger.error("", e);
                return null;
            }
        }

        if (clazz == String.class) {
            return clazz.cast(value);
        }
        return null;
    }

    public static Object unwarp(Object value) {
        if (value instanceof Integer) {
            try {
                return ((Integer) value).intValue();
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (value instanceof Short) {
            try {
                return ((Short) value).shortValue();
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (value instanceof Long) {
            try {
                return ((Long) value).longValue();
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (value instanceof Float) {
            try {
                return ((Float) value).floatValue();
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (value instanceof Double) {
            try {
                return ((Double) value).doubleValue();
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (value instanceof Boolean) {
            try {
                return ((Boolean) value).booleanValue();
            } catch (Exception e) {
                logger.error("", e);
                return null;
            }
        }

        if (value instanceof Byte) {
            try {
                return ((Byte) value).byteValue();
            } catch (NumberFormatException e) {
                logger.error("", e);
                return null;
            }
        }

        if (value instanceof Character) {
            try {
                return ((Character) value).charValue();
            } catch (Exception e) {
                logger.error("", e);
                return null;
            }
        }
        return null;
    }

    public static boolean isSameList(Class<?> actualTypes, @SuppressWarnings("rawtypes") List value) {
        if (value == null && value.isEmpty()) return true;

        boolean result = true;
        java.lang.reflect.Type[] valueActualTypes = ReflectionHelper.getActualTypes(value.getClass());
        for (Object object : value) {
            if (object instanceof String) {
                String str = (String) object;
                // todo 需要改良这里的硬编码
                if (str.contains(",")) {
                    String[] split = str.split(",");
                    if (split != null && split.length > 0) {
                        for (String s : split) {
                            if (result) {
                                result = isOrValueOf(actualTypes, s);
                            } else {
                                break;
                            }
                        }
                    }
                } else if (str.contains(";")) {
                    String[] split = str.split(";");
                    if (split != null && split.length > 0) {
                        for (String s : split) {
                            if (result) {
                                result = isOrValueOf(actualTypes, s);
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    if (result) {
                        result = isOrValueOf(actualTypes, str);
                    } else {
                        break;
                    }
                }
            } else {
                if (result) {
                    result = isOrValueOf(actualTypes, object);
                } else {
                    break;
                }
            }
        }
        return result;
    }

    public static boolean isOrValueOf(Class<?> clazz, @SuppressWarnings("rawtypes") List value) {
        if (value == null) return true;
        boolean result = true;
        for (Object object : value) {
            if (result) {
                result = isOrValueOf(clazz, object);
            } else {
                break;
            }
        }
        return result;
    }

    public static boolean isOrValueOf(java.lang.reflect.Type type, Object value) {
        boolean result = false;
        if (type instanceof Class) {
            Class clazz = (Class) type;
            if (value instanceof List) {
                return isOrValueOf(clazz, (List) value);
            } else if (value instanceof String) {
                if (type == List.class) {
                    java.lang.reflect.Type[] valueActualTypes = ReflectionHelper.getActualTypes(clazz);
                    return isSameList((Class<?>) valueActualTypes[0], (List) value);
                } else {
                    return isOrValueOf(clazz, (String) value);
                }
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            java.lang.reflect.Type rawType = parameterizedType.getRawType();
            java.lang.reflect.Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (rawType == List.class) {
                if (value instanceof List) {
                    java.lang.reflect.Type[] valueActualTypes = ReflectionHelper.getActualTypes(value.getClass());
                    if (actualTypeArguments[0] == valueActualTypes[0]) {
                        return true;
                    } else
                        return isSameList((Class<?>) actualTypeArguments[0], (List) value);
                } else {
                    return isOrValueOf((Class<?>) rawType, (List) value);
                }
            } else if (rawType instanceof Class && value instanceof String) {
                return isOrValueOf((Class<?>) rawType, (String) value);
            }
        }
        return result;
    }

    public static Class<?> getClass(java.lang.reflect.Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            java.lang.reflect.Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;
        }
        return null;
    }

    public static boolean isOrValueOf(Class<?> clazz, Object target) {
        if (target.getClass().equals(clazz)) {
            return true;
        } else if (target instanceof String) {
            String str = (String) target;
            if (NumericUtils.isInteger(str)) {
                return clazz == Integer.class || clazz == int.class || clazz == Short.class || clazz == short.class || clazz == Long.class || clazz == long.class;
            }

            if (NumericUtils.isDecimal(str)) {
                return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
            }

            return (Constants.TRUE.equalsIgnoreCase(str.trim()) || Constants.FALSE.equalsIgnoreCase(str.trim())) && (clazz == Boolean.class || clazz == boolean.class);

        }
        return false;
    }

    public static Type[] getTypes(@SuppressWarnings("rawtypes") Class[] classes) {
        if (classes == null) {
            return null;
        }
        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = Type.getType(classes[i]);
        }
        return types;
    }

    private static final Logger logger = LoggerFactory.getLogger(TypeHelper.class);
}
