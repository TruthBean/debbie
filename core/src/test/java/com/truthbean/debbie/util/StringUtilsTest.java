package com.truthbean.debbie.util;

import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

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
    }
}