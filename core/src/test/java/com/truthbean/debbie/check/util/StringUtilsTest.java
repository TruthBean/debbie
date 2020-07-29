package com.truthbean.debbie.check.util;

import com.truthbean.debbie.util.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-30 18:32.
 */
class StringUtilsTest {

    @Test
    void isChinese() {
        String text = "爱打瞌睡的程序媛喵酱";
        System.out.println(StringUtils.isChinese(text));
        String packageName = "com.truthbean.debbie";
        System.out.println(packageName.replace('.', '/'));
        System.out.println(packageName.replaceAll("\\.", "/"));
    }
}