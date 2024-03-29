package com.truthbean.debbie.time;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import static com.truthbean.debbie.time.ChineseDateTimeConstant.*;

public class ChineseDateTimeCalculator {
    public String[] sexagenaryCycle() {
        String[] result = new String[60];
        int index = 0, i = 0, j = 0;
        while (index < 60) {
            if (i == HEAVENLY_STEAMS.length) i = 0;
            if (j == EARTHLY_BRANCHES.length) j = 0;
            result[index++] = HEAVENLY_STEAMS[i] + EARTHLY_BRANCHES[j];
            i++;
            j++;
        }
        return result;
    }

    public String nextYear(ChineseYear year) {
        long index = (year.getCalendarYear() - 3) % 60;
        return SEXAGENARY_CYCLE[(int) index];
    }

    public String nextYear(long sunYear) {
        return thisYear(sunYear + 1);
    }

    public String nextYear(int sunYear) {
        return thisYear(sunYear + 1);
    }

    public static String thisYear(int sunYear) {
        int index = (sunYear - 4) % 60;
        return SEXAGENARY_CYCLE[index];
    }

    public static String thisYear(long sunYear) {
        int index = (int) ((sunYear - 4) % 60);
        return SEXAGENARY_CYCLE[index];
    }

    public static String thisYearZodiac(long sunYear) {
        int index = (int) ((sunYear - 4) % 12);
        return ZODIAC[index];
    }

    public static String thisYearZodiac(int sunYear) {
        int index = (sunYear - 4) % 12;
        return ZODIAC[index];
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(ChineseDateTimeCalculator.class);
}
