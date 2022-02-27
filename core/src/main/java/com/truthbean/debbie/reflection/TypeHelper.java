/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.common.mini.CommonConstants;
import com.truthbean.common.mini.util.ClassHelper;
import com.truthbean.common.mini.util.NumericUtils;
import com.truthbean.common.mini.util.ReflectionUtils;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-09 14:37
 */
public class TypeHelper extends ClassHelper {
    protected TypeHelper() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static Object valueOf(java.lang.reflect.Type type, Object value) {
        if (type instanceof ParameterizedType parameterizedType) {
            java.lang.reflect.Type rawType = parameterizedType.getRawType();
            java.lang.reflect.Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (rawType == List.class) {
                List result = new ArrayList<>();
                java.lang.reflect.Type typeArgument = actualTypeArguments[0];
                if (value == null)
                    return result;
                if (value instanceof List) {
                    for (Object object : (List) value) {
                        if (object instanceof String str) {
                            // todo 需要改良这里的硬编码
                            if (str.contains(",")) {
                                String[] split = str.split(",");
                                if (split.length > 0) {
                                    for (String s : split) {
                                        Object o = valueOf((Class<?>) typeArgument, s);
                                        if (o != null)
                                            result.add(o);
                                    }
                                }
                            } else if (str.contains(";")) {
                                String[] split = str.split(";");
                                if (split.length > 0) {
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
                    if (value instanceof String str) {
                        // todo 需要改良这里的硬编码
                        if (str.contains(",")) {
                            String[] split = str.split(",");
                            if (split.length > 0) {
                                for (String s : split) {
                                    Object o = valueOf((Class<?>) typeArgument, s);
                                    if (o != null)
                                        result.add(o);
                                }
                            }
                        } else if (str.contains(";")) {
                            String[] split = str.split(";");
                            if (split.length > 0) {
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
            java.lang.reflect.Type[] valueActualTypes = ReflectionUtils.getActualTypes(values.getClass());
            java.lang.reflect.Type[] typeActualTypes = ReflectionUtils.getActualTypes(clazz);
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



    public static boolean isSameList(Class<?> actualTypes, @SuppressWarnings("rawtypes") List value) {
        if (value == null || value.isEmpty()) return true;

        boolean result = true;
        // java.lang.reflect.Type[] valueActualTypes = ReflectionHelper.getActualTypes(VALUE.getClass());
        for (Object object : value) {
            if (object instanceof String str) {
                // todo 需要改良这里的硬编码
                if (str.contains(",")) {
                    String[] split = str.split(",");
                    if (split.length > 0) {
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
                    if (split.length > 0) {
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
        if (type instanceof Class<?> clazz) {
            if (value instanceof List) {
                return isOrValueOf(clazz, (List) value);
            } else if (value instanceof String) {
                if (type == List.class) {
                    java.lang.reflect.Type[] valueActualTypes = ReflectionUtils.getActualTypes(clazz);
                    return isSameList((Class<?>) valueActualTypes[0], (List) value);
                } else {
                    return isOrValueOf(clazz, (String) value);
                }
            } else {
                return clazz.isInstance(value);
            }
        } else if (type instanceof ParameterizedType parameterizedType) {
            java.lang.reflect.Type rawType = parameterizedType.getRawType();
            java.lang.reflect.Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (rawType == List.class) {
                if (value instanceof List) {
                    java.lang.reflect.Type[] valueActualTypes = ReflectionUtils.getActualTypes(value.getClass());
                    if (actualTypeArguments[0] == valueActualTypes[0]) {
                        return true;
                    } else
                        return isSameList((Class<?>) actualTypeArguments[0], (List) value);
                } else {
                    return isOrValueOf((Class<?>) rawType, (List) value);
                }
            } else if (rawType instanceof Class && value instanceof String) {
                return isOrValueOf((Class<?>) rawType, (String) value);
            } else if (rawType instanceof Class) {
                return ((Class<?>) rawType).isInstance(value);
            }
        }
        return result;
    }

    public static Class<?> getClass(java.lang.reflect.Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType parameterizedType) {
            java.lang.reflect.Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;
        }
        return null;
    }

    public static java.lang.reflect.Type[] getActualType(java.lang.reflect.Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getActualTypeArguments();
        }
        return new java.lang.reflect.Type[0];
    }

    public static boolean isOrValueOf(Class<?> clazz, Object target) {
        if (target.getClass().equals(clazz)) {
            return true;
        } else if (target instanceof String str) {
            if (NumericUtils.isInteger(str)) {
                return clazz == Integer.class || clazz == int.class || clazz == Short.class || clazz == short.class || clazz == Long.class || clazz == long.class;
            }

            if (NumericUtils.isDecimal(str)) {
                return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
            }

            return (CommonConstants.TRUE.equalsIgnoreCase(str.trim()) || CommonConstants.FALSE.equalsIgnoreCase(str.trim())) && (clazz == Boolean.class || clazz == boolean.class);

        }
        return false;
    }

    private static final Logger logger = LoggerFactory.getLogger(TypeHelper.class);
}
