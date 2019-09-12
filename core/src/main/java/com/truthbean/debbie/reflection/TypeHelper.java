package com.truthbean.debbie.reflection;

import com.truthbean.debbie.util.Constants;
import com.truthbean.debbie.util.NumericUtils;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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

    public static boolean isBaseType(Class<?> clazz) {
        return isBoolean(clazz) || isDouble(clazz) || isFloat(clazz) || isInt(clazz) || isLong(clazz) || isShort(clazz) || isChar(clazz) || isByte(clazz) || clazz == String.class || isObject(clazz);
    }

    public static boolean isRawBaseType(Class<?> clazz) {
        return boolean.class == clazz || double.class == clazz || float.class == clazz || int.class == clazz || long.class == clazz || short.class == clazz || char.class == clazz || byte.class == clazz;
    }

    public static boolean isArrayType(Class<?> clazz) {
        return clazz == Set.class || clazz == Map.class || clazz == List.class;
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

    @SuppressWarnings("unchecked")
    public static <T> List<T> valueOf(Class<T> clazz, @SuppressWarnings("rawtypes") List values) {
        if (clazz == List.class) {
            return (List<T>) values;
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
                e.printStackTrace();
                return null;
            }
        }

        if (clazz == Short.class || clazz == short.class) {
            try {
                return (T) Short.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (clazz == Long.class || long.class == clazz) {
            try {
                return (T) Long.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (clazz == Float.class || clazz == float.class) {
            try {
                return (T) Float.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (clazz == Double.class || double.class == clazz) {
            try {
                return (T) Double.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (clazz == Boolean.class || clazz == boolean.class) {
            try {
                return (T) Boolean.valueOf(value);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        if (clazz == Byte.class || clazz == byte.class) {
            try {
                return (T) Byte.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        var isChar = (clazz == Character.class || clazz == char.class) && value.length() == 1;
        if (isChar) {
            try {
                return (T) Character.valueOf(value.charAt(0));
            } catch (Exception e) {
                e.printStackTrace();
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
                e.printStackTrace();
                return null;
            }
        }

        if (value instanceof Short) {
            try {
                return ((Short) value).shortValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (value instanceof Long) {
            try {
                return ((Long) value).longValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (value instanceof Float) {
            try {
                return ((Float) value).floatValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (value instanceof Double) {
            try {
                return ((Double) value).doubleValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (value instanceof Boolean) {
            try {
                return ((Boolean) value).booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        if (value instanceof Byte) {
            try {
                return ((Byte) value).byteValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (value instanceof Character) {
            try {
                return ((Character) value).charValue();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static boolean isOrValueOf(Class<?> clazz, @SuppressWarnings("rawtypes") List target) {
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
}
