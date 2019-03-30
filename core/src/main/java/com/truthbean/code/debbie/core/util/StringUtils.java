package com.truthbean.code.debbie.core.util;

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
     * @param c The ASCII character of the hexadecimal number to decode.
     * Must be in the range {@code [0-9a-fA-F]}.
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
     * @param s string
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
     * @param obj obj
     * @return bool
     */
    public static boolean isNotEmpty(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            return !"".equals(str) && !str.isEmpty();
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
            for(int i = 0; i < strLen; ++i) {
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
}
