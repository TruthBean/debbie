package com.truthbean.debbie.time;

import org.junit.jupiter.api.Test;

class ChineseYearTest {

    @Test
    void calc() {
        ChineseDateTimeCalculator calculator = new ChineseDateTimeCalculator();
        String[] 六十甲子表 = calculator.六十甲子表();
        for (String s : 六十甲子表) {
            System.out.println(s);
        }
        System.out.println("-----------------------------------------------------------------------");
        ChineseYear year = ChineseYear.of(1803);
        System.out.println(year);
        System.out.println(year.plusYears(-100));

        System.out.println("=========================================================================");
    }
}
