package com.truthbean.debbie.check.task;

import com.truthbean.debbie.task.CronExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.2
 */
class CronExpressionTest {

    private static final ZoneOffset UTC = ZoneOffset.UTC;

    // ==================== Constructor validation ====================

    @Test
    @DisplayName("Should reject expressions with too few fields")
    void testTooFewFields() {
        assertThrows(IllegalArgumentException.class, () -> new CronExpression("* * * * *"));
    }

    @Test
    @DisplayName("Should reject expressions with too many fields")
    void testTooManyFields() {
        assertThrows(IllegalArgumentException.class, () -> new CronExpression("* * * * * * * *"));
    }

    @Test
    @DisplayName("Should accept 6-field expressions")
    void testSixFields() {
        assertDoesNotThrow(() -> new CronExpression("0 0 12 * * ?"));
    }

    @Test
    @DisplayName("Should accept 7-field expressions")
    void testSevenFields() {
        assertDoesNotThrow(() -> new CronExpression("0 0 12 * * ? 2026"));
    }

    @Test
    @DisplayName("Should reject invalid range (start > end)")
    void testInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> new CronExpression("0 0 12 * * ? 2026-2025"));
    }

    // ==================== 6-field: basic scenarios ====================

    @Test
    @DisplayName("Every day at noon")
    void testEveryDayAtNoon() {
        var cron = new CronExpression("0 0 12 * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("At second 30 of every minute")
    void testAtSecond30EveryMinute() {
        var cron = new CronExpression("30 * * * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 10, 0, 30);
    }

    @Test
    @DisplayName("At second 30, cross minute boundary")
    void testAtSecond30CrossMinute() {
        var cron = new CronExpression("30 * * * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 35, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 10, 1, 30);
    }

    @Test
    @DisplayName("Every 5 minutes")
    void testEvery5Minutes() {
        var cron = new CronExpression("0 0/5 * * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 3, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 10, 5, 0);
    }

    @Test
    @DisplayName("Every 5 minutes, cross hour boundary")
    void testEvery5MinutesCrossHour() {
        var cron = new CronExpression("0 0/5 * * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 58, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 11, 0, 0);
    }

    @Test
    @DisplayName("Weekdays 9-17 every 5 minutes")
    void testWeekdays9to17Every5Minutes() {
        // 2026-08-01 is Saturday
        var cron = new CronExpression("0 0/5 9-17 ? * 1-5");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 3, 9, 0, 0);
    }

    @Test
    @DisplayName("1st and 15th of every month at midnight")
    void test1stAnd15th() {
        var cron = new CronExpression("0 0 0 1,15 * ?");
        var now = ZonedDateTime.of(2026, 8, 10, 0, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 15, 0, 0, 0);
    }

    @Test
    @DisplayName("1st and 15th, cross month boundary")
    void test1stAnd15thCrossMonth() {
        var cron = new CronExpression("0 0 0 1,15 * ?");
        var now = ZonedDateTime.of(2026, 8, 16, 0, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 9, 1, 0, 0, 0);
    }

    @Test
    @DisplayName("Quarterly (Jan, Apr, Jul, Oct) 1st")
    void testQuarterly() {
        var cron = new CronExpression("0 0 0 1 1,4,7,10 ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 10, 1, 0, 0, 0);
    }

    // ==================== 7-field: year scenarios ====================

    @Test
    @DisplayName("7-field: specific year range")
    void testYearRange() {
        var cron = new CronExpression("0 0 12 1 * ? 2026-2028");
        // after Aug 1st, so next 1st of month is Sep 1st
        var now = ZonedDateTime.of(2026, 8, 10, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 9, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("7-field: cross year boundary")
    void testCrossYear() {
        var cron = new CronExpression("0 0 12 1 * ? 2026-2028");
        var now = ZonedDateTime.of(2028, 12, 1, 10, 0, 0, 0, UTC);
        // 2028-12-01 12:00:00 (same day, Dec 1st matches)
        assertNextTime(cron, now, 2028, 12, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("7-field: no matching year -> null")
    void testNoMatchingYear() {
        var cron = new CronExpression("0 0 12 1 * ? 2026");
        var now = ZonedDateTime.of(2027, 1, 1, 0, 0, 0, 0, UTC);
        assertNull(cron.nextTimeAfter(now));
    }

    @Test
    @DisplayName("7-field: specific year list")
    void testYearList() {
        var cron = new CronExpression("0 0 12 1 * ? 2026,2028,2030");
        // after Aug 1st, so next 1st of month is Sep 1st
        var now = ZonedDateTime.of(2026, 8, 10, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 9, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("7-field: skip to next matching year")
    void testSkipToNextYear() {
        var cron = new CronExpression("0 0 12 1 * ? 2026,2028");
        var now = ZonedDateTime.of(2026, 12, 2, 0, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2028, 1, 1, 12, 0, 0);
    }

    @Test
    @DisplayName("7-field with wildcard year")
    void testWildcardYear() {
        var cron = new CronExpression("0 0 12 * * ? *");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 12, 0, 0);
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("End of month: Feb 28 in non-leap year, March 1 should match")
    void testEndOfMonthFeb28() {
        var cron = new CronExpression("0 0 0 1 * ?");
        var now = ZonedDateTime.of(2027, 2, 1, 12, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2027, 3, 1, 0, 0, 0);
    }

    @Test
    @DisplayName("Leap year: Feb 29 exists")
    void testLeapYearFeb29() {
        var cron = new CronExpression("0 0 0 29 2 ?");
        var now = ZonedDateTime.of(2028, 1, 15, 0, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2028, 2, 29, 0, 0, 0);
    }

    @Test
    @DisplayName("Non-leap year: Feb 29 not available, should skip to next matching year")
    void testNonLeapYearFeb29() {
        var cron = new CronExpression("0 0 0 29 2 ?");
        var now = ZonedDateTime.of(2027, 2, 1, 0, 0, 0, 0, UTC);
        var next = cron.nextTimeAfter(now);
        assertEquals(2028, next.getYear());
        assertEquals(2, next.getMonthValue());
        assertEquals(29, next.getDayOfMonth());
        assertEquals(0, next.getHour());
        assertEquals(0, next.getMinute());
        assertEquals(0, next.getSecond());
    }

    @Test
    @DisplayName("Day of week: 0 and 7 both represent Sunday")
    void testDayOfWeek0And7() {
        // 2026-08-02 is Sunday
        var cron0 = new CronExpression("0 0 12 ? * 0");
        var cron7 = new CronExpression("0 0 12 ? * 7");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC); // Saturday
        assertNextTime(cron0, now, 2026, 8, 2, 12, 0, 0);
        assertNextTime(cron7, now, 2026, 8, 2, 12, 0, 0);
    }

    @Test
    @DisplayName("? in dayOfMonth: match by dayOfWeek only")
    void testQuestionMarkDayOfMonth() {
        // Every Monday at noon; 2026-08-01 is Saturday, next Monday is 2026-08-03
        // cron: 0=Sun, 1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun
        var cron = new CronExpression("0 0 12 ? * 1");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 3, 12, 0, 0);
    }

    @Test
    @DisplayName("? in dayOfWeek: match by dayOfMonth only")
    void testQuestionMarkDayOfWeek() {
        var cron = new CronExpression("0 0 12 15 * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 15, 12, 0, 0);
    }

    @Test
    @DisplayName("Both dayOfMonth and dayOfWeek specified: OR semantics")
    void testDayOfMonthAndDayOfWeekOr() {
        // 15th OR Friday at noon; 2026-08-01 is Saturday
        // next Friday is 2026-08-07, which is before 2026-08-15
        var cron = new CronExpression("0 0 12 15 * 5");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 7, 12, 0, 0);
    }

    @Test
    @DisplayName("One-shot: exact time has passed -> next day")
    void testExactTimePassed() {
        var cron = new CronExpression("0 0 12 * * ?");
        // candidate starts at 12:00:01, so next is 2026-08-02 12:00:00
        var now = ZonedDateTime.of(2026, 8, 1, 12, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 2, 12, 0, 0);
    }

    @Test
    @DisplayName("Range syntax: 8-10 hour")
    void testHourRange() {
        var cron = new CronExpression("0 0 8-10 * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 7, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 8, 0, 0);
    }

    @Test
    @DisplayName("Range syntax: cross hour boundary")
    void testHourRangeCross() {
        var cron = new CronExpression("0 0 8-10 * * ?");
        // candidate starts at 10:00:01, no matching time in 8-10 today, next is tomorrow 08:00
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 2, 8, 0, 0);
    }

    @Test
    @DisplayName("Step syntax: */15 in seconds")
    void testStepEvery15Seconds() {
        var cron = new CronExpression("0/15 * * * * ?");
        // candidate starts at 10:00:01, next match is 10:00:15
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 10, 0, 15);
    }

    @Test
    @DisplayName("Step syntax: 5/10 in seconds")
    void testStepFrom5Every10() {
        var cron = new CronExpression("5/10 * * * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 10, 0, 5);
    }

    @Test
    @DisplayName("6-field with all wildcards: every second")
    void testAllWildcards() {
        var cron = new CronExpression("* * * * * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 10, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 1, 10, 0, 1);
    }

    @Test
    @DisplayName("Cross month boundary with dayOfMonth")
    void testCrossMonthBoundary() {
        var cron = new CronExpression("0 0 0 31 * ?");
        var now = ZonedDateTime.of(2026, 8, 1, 0, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 8, 31, 0, 0, 0);
    }

    @Test
    @DisplayName("Cross month boundary: 31st not available in next month")
    void testCrossMonthBoundaryNo31() {
        var cron = new CronExpression("0 0 0 31 * ?");
        // September has no 31st, so skip to October
        var now = ZonedDateTime.of(2026, 8, 31, 12, 0, 0, 0, UTC);
        assertNextTime(cron, now, 2026, 10, 31, 0, 0, 0);
    }

    @Test
    @DisplayName("toString and equals/hashCode")
    void testToStringAndEquals() {
        var cron1 = new CronExpression("0 0 12 * * ?");
        var cron2 = new CronExpression("0 0 12 * * ?");
        var cron3 = new CronExpression("0 30 12 * * ?");
        assertEquals("0 0 12 * * ?", cron1.toString());
        assertEquals(cron1, cron2);
        assertEquals(cron1.hashCode(), cron2.hashCode());
        assertNotEquals(cron1, cron3);
        assertNotEquals(cron1, null);
        assertNotEquals(cron1, "not a cron");
    }

    // ==================== Helper ====================

    private void assertNextTime(CronExpression cron, ZonedDateTime now,
                                int year, int month, int day,
                                int hour, int minute, int second) {
        var next = cron.nextTimeAfter(now);
        assertNotNull(next, () -> "Expected next time for " + cron + " after " + now);
        assertEquals(year, next.getYear(), "year mismatch");
        assertEquals(month, next.getMonthValue(), "month mismatch");
        assertEquals(day, next.getDayOfMonth(), "day mismatch");
        assertEquals(hour, next.getHour(), "hour mismatch");
        assertEquals(minute, next.getMinute(), "minute mismatch");
        assertEquals(second, next.getSecond(), "second mismatch");
    }
}