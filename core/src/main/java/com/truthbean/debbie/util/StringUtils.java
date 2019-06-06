package com.truthbean.debbie.util;

import java.util.*;

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

    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
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

    public static String trim(String target, String prefix, String prefixOverrides, String suffix, String suffixOverrides) {
        if (target == null) return null;
        StringBuilder stringBuilder = new StringBuilder();
        if (prefixOverrides != null && !prefixOverrides.isBlank()) {
            int prefixOverridesLength = prefixOverrides.length();
            if (target.startsWith(prefixOverrides)) {
                target = target.substring(prefixOverridesLength);
            }
        }
        if (suffixOverrides != null && !suffixOverrides.isBlank()) {
            int suffixOverridesLength = target.lastIndexOf(suffixOverrides);
            if (target.endsWith(suffixOverrides)) {
                target = target.substring(0, suffixOverridesLength);
            }
        }

        if (prefix != null) {
            stringBuilder.append(prefix);
        }
        stringBuilder.append(target);
        if (suffix != null) {
            stringBuilder.append(suffix);
        }
        return stringBuilder.toString();
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return new String[0];
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection<String> collection) {
        return (collection != null ? collection.toArray(new String[0]) : new String[0]);
    }

    // ===============================================================================================================

    public static <T> String joining(List<T> list) {
        return joining(list, ",");
    }

    public static <T> String joining(Collection<T> list, String split) {
        StringBuilder builder = new StringBuilder();
        joining(builder, list, split);
        return builder.toString();
    }

    public static <T> void joining(StringBuilder builder, Collection<T> list, String split) {
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            Iterator<T> iterator = list.iterator();
            for (int i = 0; i < size - 1; i++) {
                T e = iterator.next();
                if (e != null) {
                    builder.append(e).append(split);
                }
            }
            T e = iterator.next();
            if (e != null) {
                builder.append(e);
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

    public static String snakeCaseToCamelCaseTo(String snakeCaseString) {
        char[] chars = snakeCaseString.toCharArray();
        char[] newChars = new char[chars.length];
        int j = 0;
        for (int i = 0, charsLength = chars.length; i < charsLength;) {
            char c = chars[i++];
            if (c == '-') {
                c = chars[i++];
                if (c >= 'a' && c <= 'z') {
                    newChars[j++] = (char) (c - 32);
                }
            } else {
                newChars[j++] = c;
            }
        }
        char[] target = new char[j];
        System.arraycopy(newChars, 0, target, 0, j);
        return new String(target);
    }
}
