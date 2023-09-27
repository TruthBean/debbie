package com.truthbean.debbie.time;

import org.junit.jupiter.api.Test;

class ChineseYearTest {

    @Test
    void calc() {
        ChineseDateTimeCalculator calculator = new ChineseDateTimeCalculator();
        String[] sexagenaryCycle = calculator.sexagenaryCycle();
        for (String s : sexagenaryCycle) {
            System.out.println(s);
        }
        System.out.println("-----------------------------------------------------------------------");
        ChineseYear year = ChineseYear.of(1803);
        System.out.println(year.toChineseString());
        System.out.println(year.plusYears(-100).toChineseString());

        System.out.println("=========================================================================");
    }
}
