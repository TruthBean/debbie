/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.util;

import java.sql.Array;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-11 11:03
 */
public final class StringUtils {
    private static final Pattern CHINESE = Pattern.compile("^[\\u4e00-\\u9FEF]*$");
    private static final Pattern PATTERN = Pattern.compile("[0-9]*");

    private StringUtils() {
    }

    public static boolean isChinese(String text) {
        return CHINESE.matcher(text).find();
    }

    public static boolean isNumeric(String str) {
        Matcher isNum = PATTERN.matcher(str);
        return isNum.matches();
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

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check whether the given {@code CharSequence} contains actual <em>text</em>.
     * &lt;p&gt;More specifically, this method returns {@code true} if the
     * {@code CharSequence} is not {@code null}, its length is greater than
     * 0, and it contains at least one non-whitespace character.
     * &lt;p&gt;&lt;pre class="code"&gt;
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * &lt;/pre&gt;
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null},
     * its length is greater than 0, and it does not contain whitespace only
     * @see Character#isWhitespace
     */
    public static boolean hasText(CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    /**
     * Check whether the given {@code String} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code String} is not {@code null}, its length is greater than 0,
     * and it contains at least one non-whitespace character.
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null}, its
     * length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     */
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
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

    /**
     * 去掉指定后缀
     *
     * @param str 字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str == null ? null : "";
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return substring(str2, 0,str2.length() - suffix.length());
        }
        return str2;
    }

    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            // remember start is negative
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }

        return str.substring(start);
    }

    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            // remember end is negative
            end = str.length() + end;
        }
        if (start < 0) {
            // remember start is negative
            start = str.length() + start;
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
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

    /**
     * Test if the given {@code String} starts with the specified prefix,
     * ignoring upper/lower case.
     * @param str the {@code String} to check
     * @param prefix the prefix to look for
     * @see java.lang.String#startsWith
     * @return boolean
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return (str != null && prefix != null && str.length() >= prefix.length() &&
                str.regionMatches(true, 0, prefix, 0, prefix.length()));
    }

    // ===============================================================================================================

    @SuppressWarnings("unchecked")
    public static <T> String joining(final T... elements) {
        return joining(elements, "");
    }

    public static <T> String joining(List<T> list) {
        return joining(list, ",");
    }

    public static <T> String joining(Collection<T> iterator, String split) {
        StringBuilder builder = new StringBuilder();
        joining(builder, iterator, split);
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

    public static <T> void joining(StringBuilder builder, Iterator<T> iterator, String split) {
        if (iterator != null && iterator.hasNext()) {
            while (iterator.hasNext()) {
                T e = iterator.next();
                if (e != null) {
                    builder.append(e).append(split);
                }
            }
            builder.deleteCharAt(builder.lastIndexOf(split));
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
        if (string == null || string.isBlank())
            return "";
        String first = string.substring(0, 1).toLowerCase();
        return first + string.substring(1);
    }

    public static String snakeCaseToCamelCaseTo(String snakeCaseString) {
        char[] chars = snakeCaseString.toCharArray();
        char[] newChars = new char[chars.length];
        int j = 0;
        for (int i = 0, charsLength = chars.length; i < charsLength; ) {
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

    /**
     * If the {@code obj} is an array, toString() method of {@link Arrays} is called. Otherwise
     * {@link Object#toString()} is called. Returns "null" if {@code obj} is <code>null</code>.
     *
     * @param obj
     *          An object. May be an array or <code>null</code>.
     * @return String representation of the {@code obj}.
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        final Class<?> clazz = obj.getClass();
        if (!clazz.isArray()) {
            return obj.toString();
        }
        final Class<?> componentType = obj.getClass().getComponentType();
        if (long.class.equals(componentType)) {
            return Arrays.toString((long[]) obj);
        } else if (int.class.equals(componentType)) {
            return Arrays.toString((int[]) obj);
        } else if (short.class.equals(componentType)) {
            return Arrays.toString((short[]) obj);
        } else if (char.class.equals(componentType)) {
            return Arrays.toString((char[]) obj);
        } else if (byte.class.equals(componentType)) {
            return Arrays.toString((byte[]) obj);
        } else if (boolean.class.equals(componentType)) {
            return Arrays.toString((boolean[]) obj);
        } else if (float.class.equals(componentType)) {
            return Arrays.toString((float[]) obj);
        } else if (double.class.equals(componentType)) {
            return Arrays.toString((double[]) obj);
        } else {
            return Arrays.toString((Object[]) obj);
        }
    }

    public static String objectValueString(Object value) {
        if (value instanceof Array) {
            try {
                return toString(((Array) value).getArray());
            } catch (SQLException e) {
                return value.toString();
            }
        }
        return value.toString();
    }

    public static String getParameterValueString(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        List<Object> typeList = new ArrayList<>(args.length);
        for (Object value : args) {
            if (value == null) {
                typeList.add("null");
            } else {
                typeList.add(StringUtils.objectValueString(value) + "(" + value.getClass().getSimpleName() + ")");
            }
        }
        final String parameters = typeList.toString();
        return parameters.substring(1, parameters.length() - 1);
    }

    public static Collection<String> split(String raw, String split) {
        List<String> result = new ArrayList<>();
        if(raw.contains(split)) {
            String[] strs = raw.split(split);
            result.addAll(Arrays.asList(strs));
        } else {
            result.add(raw);
        }
        return result;
    }
}
