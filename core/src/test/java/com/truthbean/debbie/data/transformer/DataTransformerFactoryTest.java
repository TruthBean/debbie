package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.test.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-03-04 10:17
 */
@DebbieApplicationTest
class DataTransformerFactoryTest {

    @Test
    void transform() {
        HashSet<String> set = new HashSet<>();
        set.add("酱");
        set.add("卡哇伊");
        set.add("beautiful");
        String transform = DataTransformerFactory.transform(set, String.class);
        System.out.println(transform);

        /*BigDecimal bigDecimal = new BigDecimal("20200304.0989");
        Long l = DataTransformerFactory.transform(bigDecimal, Long.class);
        System.out.println(l);*/
    }

    @Test
    void stringArrayToString() {
        String[] strings = new String[2];
        strings[0] = "0000";
        strings[1] = "11111111";
        String transform = DataTransformerFactory.transform(strings, String.class);
        System.out.println(transform);
    }
}