package com.truthbean.debbie.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-09 14:43
 */
public final class NumericUtils {
    private NumericUtils() {
    }

    public static final Pattern INTEGER_PATTERN = Pattern.compile("[0-9]*");

    public static final Pattern DECIMAL_PATTERN = Pattern.compile("^[0-9]+\\.[0-9]*");

    public static boolean isDecimal(String str) {
        Matcher isNum = DECIMAL_PATTERN.matcher(str);
        return isNum.matches();
    }

    public static boolean isInteger(String str) {
        Matcher isNum = INTEGER_PATTERN.matcher(str);
        return isNum.matches();
    }
}
