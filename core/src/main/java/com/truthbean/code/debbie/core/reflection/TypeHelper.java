package com.truthbean.code.debbie.core.reflection;

import com.truthbean.code.debbie.core.util.Constants;
import com.truthbean.code.debbie.core.util.NumericUtils;

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

    static {
        CLASS_MAP.put(Integer.class.getName(), Integer.class);
        CLASS_MAP.put(int.class.getName(), int.class);
        CLASS_MAP.put(Short.class.getName(), Short.class);
        CLASS_MAP.put(short.class.getName(), short.class);
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

    public static boolean isBaseType(Class<?> clazz) {
        return isBoolean(clazz) || isDouble(clazz) || isFloat(clazz) || isInt(clazz) || isLong(clazz) || isShort(clazz)
                || isChar(clazz) || isByte(clazz) || clazz == String.class || isObject(clazz);
    }

    public static boolean isArrayType(Class<?> clazz) {
        return clazz == Set.class || clazz == Map.class || clazz == List.class;
    }

    public static boolean isOrValueOf(Class<?> clazz, String target) {
        if (target.getClass().equals(clazz)) {
            return true;
        } else {
            if (NumericUtils.isInteger(target)) {
                return clazz == Integer.class || clazz == int.class || clazz == Short.class || clazz == short.class
                        || clazz == Long.class || clazz == long.class;
            }

            if (NumericUtils.isDecimal(target)) {
                return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
            }

            return (Constants.TRUE.equalsIgnoreCase(target.trim()) || Constants.FALSE.equalsIgnoreCase(target.trim()))
                    && (clazz == Boolean.class || clazz == boolean.class);

        }
    }

    public static <T> List<T> valueOf(Class<T> clazz, List values) {
        if (clazz == List.class) {
            return (List<T>) values;
        }
        List<T> result = new ArrayList<>();
        for (Object object : values) {
            if (object.getClass() == clazz) {
                result.add((T) object);
            } else if (object instanceof String) {
                result.add(valueOf(clazz, (String) object));
            } else {
                result.add((T) object);
            }
        }
        return result;
    }

    public static <T> T valueOf(Class<T> clazz, Object value) {
        if (value.getClass() == clazz) {
            return (T) value;
        } else if (value instanceof String) {
            return valueOf(clazz, (String) value);
        }

        return (T) value;
    }

    public static <T> T valueOf(Class<T> clazz, String value) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(value);
        }

        if (clazz == Short.class || clazz == short.class) {
            return (T) Short.valueOf(value);
        }

        if (clazz == Long.class || long.class == clazz) {
            return (T) Long.valueOf(value);
        }

        if (clazz == Float.class || clazz == float.class) {
            return (T) Float.valueOf(value);
        }

        if (clazz == Double.class || double.class == clazz) {
            return (T) Double.valueOf(value);
        }

        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(value);
        }

        if (clazz == Byte.class || clazz == byte.class) {
            return (T) Byte.valueOf(value);
        }

        if ((clazz == Character.class || clazz == char.class) && value.length() == 1) {
            return (T) Character.valueOf(value.charAt(0));
        }

        if (clazz == String.class) {
            return (T) value;
        }
        return null;
    }

    public static boolean isOrValueOf(Class<?> clazz, List target) {
        boolean result = true;
        if (clazz == List.class) {
            return true;
        }
        for (Object object : target) {
            if (result) {
                result = isOrValueOf(clazz, object);
            } else {
                break;
            }
        }
        return result;
    }

    public static boolean isOrValueOf(Class<?> clazz, Object target) {
        if (target.getClass().equals(clazz)) {
            return true;
        } else if (target instanceof String) {
            String str = (String) target;
            if (NumericUtils.isInteger(str)) {
                return clazz == Integer.class || clazz == int.class || clazz == Short.class || clazz == short.class
                        || clazz == Long.class || clazz == long.class;
            }

            if (NumericUtils.isDecimal(str)) {
                return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
            }

            return (Constants.TRUE.equalsIgnoreCase(str.trim()) || Constants.FALSE.equalsIgnoreCase(str.trim()))
                    && (clazz == Boolean.class || clazz == boolean.class);

        }
        return false;
    }
}
