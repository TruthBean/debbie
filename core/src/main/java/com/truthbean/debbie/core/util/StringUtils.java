package com.truthbean.debbie.core.util;

import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-11 11:03
 */
public final class StringUtils {
    private StringUtils() {
    }

    /**
     * Helper to decode half of a hexadecimal number from a string.
     *
     * @param c The ASCII character of the hexadecimal number to decode.
     *          Must be in the range {@code [0-9a-fA-F]}.
     * @return The hexadecimal value represented in the ASCII character
     * given, or {@code -1} if the character is invalid.
     */
    public static int decodeHexNibble(final char c) {
        // Character.digit() is not used here, as it addresses a larger
        // set of characters (both ASCII and full-width latin letters).
        if (c >= Constants.ZERO && c <= Constants.NINE) {
            return c - '0';
        }
        if (c >= Constants.UPPERCASE_A && c <= Constants.UPPERCASE_F) {
            return c - (Constants.UPPERCASE_A - 0xA);
        }
        if (c >= Constants.LOWERCASE_A && c <= Constants.LOWERCASE_F) {
            return c - (Constants.LOWERCASE_A - 0xA);
        }
        return -1;
    }

    /**
     * Decode a 2-digit hex byte from within a string.
     *
     * @param s   string
     * @param pos position
     * @return byte
     */
    public static byte decodeHexByte(CharSequence s, int pos) {
        int hi = decodeHexNibble(s.charAt(pos));
        int lo = decodeHexNibble(s.charAt(pos + 1));
        if (hi == -1 || lo == -1) {
            throw new IllegalArgumentException(String.format(
                    "invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
        }
        return (byte) ((hi << 4) + lo);
    }

    /**
     * is str not empty
     *
     * @param obj obj
     * @return bool
     */
    public static boolean isNotEmpty(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            return !str.isBlank();
        } else {
            return false;
        }
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    // ===============================================================================================================

    public static <T> String joining(List<T> list) {
        return joining(list, ",");
    }

    public static <T> String joining(List<T> list, String split) {
        StringBuilder builder = new StringBuilder();
        joining(builder, list, split);
        return builder.toString();
    }

    public static <T> void joining(StringBuilder builder, List<T> list, String split) {
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            for (int i = 0; i < size - 1; i++) {
                if (list.get(i) != null) {
                    builder.append(list.get(i).toString()).append(split);
                }
            }
            if (list.get(size - 1) != null) {
                builder.append(list.get(size - 1).toString());
            }
        }
    }

    public static <T> String joining(T[] array, String split) {
        StringBuilder builder = new StringBuilder();
        joining(array, split, builder);
        return builder.toString();
    }

    public static <T> void joining(T[] array, String split, StringBuilder builder) {
        if (array != null && array.length > 0) {
            int size = array.length;
            for (int i = 0; i < size - 1; i++) {
                if (array[i] != null) {
                    builder.append(array[i]).append(split);
                }
            }
            if (array[size - 1] != null) {
                builder.append(array[size - 1]);
            }
        }
    }

    public static <K, V> void joining(Map<K, V> map, String split, String elementSplit, StringBuilder builder) {
        if (map != null && !map.isEmpty()) {
            int size = map.size();
            var keys = map.keySet().iterator();
            for (int i = 0; i < size - 1; i++) {
                var k = keys.next();
                var v = map.get(k);
                builder.append(k).append(elementSplit).append(v);
                builder.append(split);
            }

            var k = keys.next();
            var v = map.get(k);
            builder.append(k).append(elementSplit).append(v);
        }
    }

    public static <K, T> void joining(Map<K, List<T>> map,
                                       String split, String elementSplit, String valueSplit,
                                       StringBuilder builder) {
        if (map != null && !map.isEmpty()) {
            int size = map.size();
            var keys = map.keySet().iterator();
            for (int i = 0; i < size - 1; i++) {
                var k = keys.next();
                builder.append(k).append(elementSplit);

                var v = map.get(k);
                if (v != null && !v.isEmpty()) {
                    joining(builder, v, valueSplit);
                }
                builder.append(split);
            }

            var k = keys.next();
            builder.append(keys.next()).append(elementSplit);
            var v = map.get(k);
            if (v != null && !v.isEmpty()) {
                joining(builder, v, valueSplit);
            }
        }
    }

    public static <K, T> void joiningWithMultiKey(Map<K, List<T>> map,
                                      String split, String elementSplit,
                                      StringBuilder builder) {
        if (map != null && !map.isEmpty()) {
            int size = map.size();
            var keys = map.keySet().iterator();
            for (int i = 0; i < size - 1; i++) {
                var k = keys.next();
                builder.append(k).append(elementSplit);

                var v = map.get(k);
                var valueSplit = k + elementSplit + split;
                if (v != null && !v.isEmpty()) {
                    joining(builder, v, valueSplit);
                }
                builder.append(split);
            }

            var k = keys.next();
            builder.append(keys.next()).append(elementSplit);
            var valueSplit = k + elementSplit + split;
            var v = map.get(k);
            if (v != null && !v.isEmpty()) {
                joining(builder, v, valueSplit);
            }
        }
    }

    public static String toFirstCharLowerCase(String string) {
        String first = string.substring(0, 1).toLowerCase();
        return first + string.substring(1);
    }
}
