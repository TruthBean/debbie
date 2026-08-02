/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import java.time.ZonedDateTime;
import java.util.BitSet;

/**
 * Parse and compute next execution time for a cron expression.
 * <p>
 * 支持 6 字段和 7 字段两种格式：
 * <ul>
 *   <li><b>6 字段:</b> {@code second minute hour dayOfMonth month dayOfWeek}</li>
 *   <li><b>7 字段:</b> {@code second minute hour dayOfMonth month dayOfWeek year}</li>
 * </ul>
 * <p>
 * <b>字段说明:</b>
 * <table border="1">
 *   <caption>cron field description</caption>
 *   <tr><th>字段</th><th>必填</th><th>取值范围</th><th>特殊字符</th></tr>
 *   <tr><td>second</td><td>是</td><td>0-59</td><td>{@code *} {@code -} {@code ,} {@code /}</td></tr>
 *   <tr><td>minute</td><td>是</td><td>0-59</td><td>{@code *} {@code -} {@code ,} {@code /}</td></tr>
 *   <tr><td>hour</td><td>是</td><td>0-23</td><td>{@code *} {@code -} {@code ,} {@code /}</td></tr>
 *   <tr><td>dayOfMonth</td><td>是</td><td>1-31</td><td>{@code *} {@code ?} {@code -} {@code ,} {@code /}</td></tr>
 *   <tr><td>month</td><td>是</td><td>1-12</td><td>{@code *} {@code -} {@code ,} {@code /}</td></tr>
 *   <tr><td>dayOfWeek</td><td>是</td><td>0-7 (0 和 7 都表示周日)</td><td>{@code *} {@code ?} {@code -} {@code ,} {@code /}</td></tr>
 *   <tr><td>year</td><td>否</td><td>1970-2199</td><td>{@code *} {@code -} {@code ,}</td></tr>
 * </table>
 * <p>
 * <b>特殊字符说明:</b>
 * <ul>
 *   <li><b>{@code *}</b> - 所有值。例如 {@code *} 在 minute 字段表示每分钟。</li>
 *   <li><b>{@code ?}</b> - 无特定值。用于 dayOfMonth 或 dayOfWeek 字段，表示不限制该字段。
 *       当其中一个字段指定了具体值时，另一个通常设为 {@code ?} 以避免冲突。</li>
 *   <li><b>{@code N}</b> - 单个具体值。例如 {@code 5} 在 hour 字段表示 5:00。</li>
 *   <li><b>{@code N-M}</b> - 范围。例如 {@code 1-5} 在 dayOfWeek 字段表示周一到周五。</li>
 *   <li><b>{@code N,M,...}</b> - 枚举列表。例如 {@code 1,15} 在 dayOfMonth 字段表示每月 1 号和 15 号。</li>
 *   <li><b>{@code N/M}</b> 或 <b>{@code *}{@code /M}</b> - 步进。从 N 开始，每 M 个值执行一次。
 *       例如 {@code 0/15} 在 minute 字段表示每 15 分钟执行（0, 15, 30, 45）。</li>
 * </ul>
 * <p>
 * <b>dayOfMonth 与 dayOfWeek 的交互:</b>
 * 当两个字段都指定了具体值（即不是 {@code *} 也不是 {@code ?}）时，日期匹配采用 OR 语义，
 * 即只要其中一个字段匹配即可。例如 {@code 0 0 12 15 * 5} 表示每月 15 号或每周五的中午 12:00 执行。
 * 如果想指定具体日期，建议将另一个字段设为 {@code ?}，例如 {@code 0 0 12 15 * ?} 表示每月 15 号。
 * <p>
 * <b>常用示例:</b>
 * <pre>
 * {@code
 * // 每天中午 12:00:00 执行
 * "0 0 12 * * ?"
 *
 * // 每 5 分钟执行一次
 * "0 0/5 * * * ?"
 *
 * // 每分钟的第 30 秒执行
 * "30 * * * * ?"
 *
 * // 工作日上午 9:00 到下午 5:00 之间每 5 分钟执行
 * "0 0/5 9-17 ? * 1-5"
 *
 * // 每月 1 号和 15 号的凌晨 0:00 执行
 * "0 0 0 1,15 * ?"
 *
 * // 每季度第一天（1 月、4 月、7 月、10 月的 1 号）执行
 * "0 0 0 1 1,4,7,10 ?"
 *
 * // 7 字段：仅在 2026-2028 年之间，每月 1 号中午 12:00 执行
 * "0 0 12 1 * ? 2026-2028"
 * }
 * </pre>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.2
 */
public class CronExpression {

    private final String expression;
    private final BitSet seconds = new BitSet(60);
    private final BitSet minutes = new BitSet(60);
    private final BitSet hours = new BitSet(24);
    private final BitSet daysOfMonth = new BitSet(32);
    private final BitSet months = new BitSet(13);
    private final BitSet daysOfWeek = new BitSet(8);
    private final BitSet years = new BitSet(230);
    private final boolean dayOfMonthSpecified;
    private final boolean dayOfWeekSpecified;
    private final boolean yearSpecified;

    private static final int YEAR_MIN = 1970;
    private static final int YEAR_MAX = 2199;

    /**
     * Construct a CronExpression from a 6-field or 7-field cron string.
     * <p>
     * 6-field: {@code second minute hour dayOfMonth month dayOfWeek}
     * <br>
     * 7-field: {@code second minute hour dayOfMonth month dayOfWeek year}
     *
     * @param expression the cron expression (6 or 7 fields)
     * @throws IllegalArgumentException if the expression is invalid
     */
    public CronExpression(String expression) {
        this.expression = expression;
        String[] fields = expression.trim().split("\\s+");
        if (fields.length < 6 || fields.length > 7) {
            throw new IllegalArgumentException(
                    "Cron expression must have 6 or 7 fields separated by spaces: " +
                    "second minute hour dayOfMonth month dayOfWeek [year]. Got: " + expression);
        }
        parseField(fields[0], 0, 59, seconds);
        parseField(fields[1], 0, 59, minutes);
        parseField(fields[2], 0, 23, hours);
        dayOfMonthSpecified = parseField(fields[3], 1, 31, daysOfMonth);
        parseField(fields[4], 1, 12, months);
        dayOfWeekSpecified = parseField(fields[5], 0, 7, daysOfWeek);
        // Normalize: 0 and 7 both represent Sunday
        if (daysOfWeek.get(7)) {
            daysOfWeek.set(0);
        }
        if (fields.length == 7) {
            yearSpecified = parseField(fields[6], YEAR_MIN, YEAR_MAX, years);
        } else {
            yearSpecified = false;
            for (int i = YEAR_MIN; i <= YEAR_MAX; i++) {
                years.set(i);
            }
        }
    }

    /**
     * Parse a single cron field into a BitSet of valid values.
     *
     * @param field the field string
     * @param min   minimum valid value
     * @param max   maximum valid value
     * @param bits  the BitSet to populate
     * @return true if the field specifies concrete values
     */
    private boolean parseField(String field, int min, int max, BitSet bits) {
        if ("*".equals(field)) {
            for (int i = min; i <= max; i++) {
                bits.set(i);
            }
            return true;
        }
        if ("?".equals(field)) {
            for (int i = min; i <= max; i++) {
                bits.set(i);
            }
            return false;
        }
        // Handle comma-separated list
        String[] parts = field.split(",");
        for (String part : parts) {
            parseFieldPart(part.trim(), min, max, bits);
        }
        return true;
    }

    /**
     * Parse a single part of a cron field (e.g. "1-5", "&#42;/5", "10").
     */
    private void parseFieldPart(String part, int min, int max, BitSet bits) {
        int slashIndex = part.indexOf('/');
        int step = 1;
        String rangePart;

        if (slashIndex > 0) {
            step = Integer.parseInt(part.substring(slashIndex + 1));
            rangePart = part.substring(0, slashIndex);
        } else {
            rangePart = part;
        }

        int rangeStart;
        int rangeEnd;
        int hyphenIndex = rangePart.indexOf('-');
        if ("*".equals(rangePart)) {
            rangeStart = min;
            rangeEnd = max;
        } else if (hyphenIndex > 0) {
            rangeStart = Integer.parseInt(rangePart.substring(0, hyphenIndex));
            rangeEnd = Integer.parseInt(rangePart.substring(hyphenIndex + 1));
            if (rangeStart > rangeEnd) {
                throw new IllegalArgumentException("Invalid range in cron expression: " + part);
            }
        } else {
            rangeStart = Integer.parseInt(rangePart);
            // When there's a step (e.g. "0/5"), the range extends to max
            if (slashIndex > 0) {
                rangeEnd = max;
            } else {
                rangeEnd = rangeStart;
            }
        }

        for (int i = rangeStart; i <= rangeEnd; i += step) {
            if (i >= min && i <= max) {
                bits.set(i);
            }
        }
    }

    /**
     * Compute the next execution time after the given time.
     *
     * @param after the reference time
     * @return the next execution time, or null if no match found within a reasonable bound
     */
    public ZonedDateTime nextTimeAfter(ZonedDateTime after) {
        ZonedDateTime candidate = after.withNano(0).plusSeconds(1);

        // Iterate until we find a match or give up.
        // Each iteration advances at least one field, so this converges quickly.
        for (int iteration = 0; iteration < 100; iteration++) {
            // 0. Match year
            int year = candidate.getYear();
            int nextYear = findNextSetBit(years, year, YEAR_MIN, YEAR_MAX);
            if (nextYear == -1) {
                // No matching year within range
                return null;
            }
            if (nextYear != year) {
                candidate = candidate.withYear(nextYear)
                        .withMonth(1).withDayOfMonth(1)
                        .withHour(0).withMinute(0).withSecond(0);
                continue;
            }

            // 1. Match month
            int month = candidate.getMonthValue();
            int nextMonth = findNextSetBit(months, month, 1, 12);
            if (nextMonth == -1) {
                // No matching month this year, advance to next year January
                candidate = candidate.plusYears(1)
                        .withMonth(1).withDayOfMonth(1)
                        .withHour(0).withMinute(0).withSecond(0);
                continue;
            }
            if (nextMonth != month) {
                candidate = candidate.withDayOfMonth(1).withMonth(nextMonth)
                        .withHour(0).withMinute(0).withSecond(0);
                continue;
            }

            // 2. Match day (dayOfMonth and/or dayOfWeek)
            if (!matchesDay(candidate)) {
                int dayOfMonth = candidate.getDayOfMonth();
                int maxDay = candidate.toLocalDate().lengthOfMonth();
                if (dayOfMonth >= maxDay) {
                    candidate = candidate.plusMonths(1)
                            .withDayOfMonth(1)
                            .withHour(0).withMinute(0).withSecond(0);
                } else {
                    candidate = candidate.plusDays(1)
                            .withHour(0).withMinute(0).withSecond(0);
                }
                continue;
            }

            // 3. Match hour
            int hour = candidate.getHour();
            int nextHour = findNextSetBit(hours, hour, 0, 23);
            if (nextHour == -1) {
                // No matching hour today, advance to next day
                candidate = candidate.plusDays(1)
                        .withHour(0).withMinute(0).withSecond(0);
                continue;
            }
            if (nextHour != hour) {
                candidate = candidate.withHour(nextHour).withMinute(0).withSecond(0);
                continue;
            }

            // 4. Match minute
            int minute = candidate.getMinute();
            int nextMinute = findNextSetBit(minutes, minute, 0, 59);
            if (nextMinute == -1) {
                // No matching minute this hour, advance to next hour
                candidate = candidate.plusHours(1).withMinute(0).withSecond(0);
                continue;
            }
            if (nextMinute != minute) {
                candidate = candidate.withMinute(nextMinute).withSecond(0);
                continue;
            }

            // 5. Match second
            int second = candidate.getSecond();
            int nextSecond = findNextSetBit(seconds, second, 0, 59);
            if (nextSecond == -1) {
                // No matching second this minute, advance to next minute
                candidate = candidate.plusMinutes(1).withSecond(0);
                continue;
            }
            if (nextSecond != second) {
                candidate = candidate.withSecond(nextSecond);
                continue;
            }

            // All fields match
            return candidate;
        }
        // Safety guard: no match found within loop bound
        return null;
    }

    /**
     * Check whether the candidate's day matches the cron expression's day constraints.
     * <p>
     * When both dayOfMonth and dayOfWeek are specified, the day matches if EITHER matches.
     */
    private boolean matchesDay(ZonedDateTime candidate) {
        int dayOfMonth = candidate.getDayOfMonth();
        int dow = candidate.getDayOfWeek().getValue() % 7; // 0=Sun, 1=Mon, ..., 6=Sat

        boolean dayOfMonthOk = !dayOfMonthSpecified || daysOfMonth.get(dayOfMonth);
        boolean dayOfWeekOk = !dayOfWeekSpecified || daysOfWeek.get(dow);

        if (dayOfMonthSpecified && dayOfWeekSpecified) {
            // Both specified: day matches if either matches
            return dayOfMonthOk || dayOfWeekOk;
        }
        // Only one or neither specified
        return dayOfMonthOk && dayOfWeekOk;
    }

    /**
     * Find the next set bit in the BitSet starting from the current value.
     *
     * @param bits    the BitSet to search
     * @param current the starting value (inclusive)
     * @param min     minimum valid value (unused, for documentation)
     * @param max     maximum valid value
     * @return the next set bit in [current, max], or -1 if none found
     */
    private int findNextSetBit(BitSet bits, int current, int min, int max) {
        int next = bits.nextSetBit(current);
        if (next == -1 || next > max) {
            return -1;
        }
        return next;
    }

    @Override
    public String toString() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CronExpression that = (CronExpression) o;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }
}