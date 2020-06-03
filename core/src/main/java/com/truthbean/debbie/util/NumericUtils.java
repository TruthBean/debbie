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
