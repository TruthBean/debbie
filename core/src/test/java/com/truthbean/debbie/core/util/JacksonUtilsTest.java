package com.truthbean.debbie.core.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 17:06
 */
class JacksonUtilsTest {

    @Test
    void toXml() {
        Map<String, List<String>> value = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        value.put("key1", list);
        System.out.println(JacksonUtils.toXml(value));
        System.out.println(list);
    }

    @Test
    void xmlToBean() {
        var text = "<key1>value</key1>";
        System.out.println(JacksonUtils.xmlToJsonNode(text));
    }

    @Test
    void xmlStreamToBean() {
        var text = "<map><key1>value</key1><key1>value</key1></map>";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        List list = JacksonUtils.xmlStreamToBean(inputStream, List.class);
        System.out.println(list.toString());
    }
}