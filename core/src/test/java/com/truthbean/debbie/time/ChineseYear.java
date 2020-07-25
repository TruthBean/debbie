package com.truthbean.debbie.time;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.*;
import java.util.Objects;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.*;

public final class ChineseYear implements Temporal, TemporalAdjuster, Comparable<ChineseYear>, Serializable {
    /**
     * sun year
     * 公历年
     */
    private final long sunYear;

    /**
     * chinese year
     * 农历年
     */
    private final String year;

    private final String tiangan;

    private final String dizhi;

    private final String shengxiao;

    private ChineseYear(long sunYear) {
        this.sunYear = sunYear;
        this.year = ChineseDateTimeCalculator.thisYear(sunYear);
        tiangan = String.valueOf(year.charAt(0));
        dizhi = String.valueOf(year.charAt(1));
        shengxiao = ChineseDateTimeCalculator.thisYearZodiac(sunYear);
    }

    private ChineseYear(int sunYear) {
        this.sunYear = sunYear;
        this.year = ChineseDateTimeCalculator.thisYear(sunYear);
        tiangan = String.valueOf(year.charAt(0));
        dizhi = String.valueOf(year.charAt(1));
        shengxiao = ChineseDateTimeCalculator.thisYearZodiac(sunYear);
    }

    public String getYear() {
        return year;
    }

    public Year toYear() {
        YEAR.checkValidValue(sunYear);
        return Year.of((int) sunYear);
    }


    public Long getSunYear() {
        return sunYear;
    }

    public String getTiangan() {
        return tiangan;
    }

    public String getDizhi() {
        return dizhi;
    }

    public String getShengxiao() {
        return shengxiao;
    }

    @Override
    public int compareTo(ChineseYear o) {
        return (int) (sunYear - o.sunYear);
    }

    @Override
    public Temporal adjustInto(Temporal temporal) {
        if (Chronology.from(temporal).equals(IsoChronology.INSTANCE) == false) {
            throw new DateTimeException("Adjustment only supported on ISO date-time");
        }
        return temporal.with(YEAR, sunYear);
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            return unit == YEARS || unit == DECADES || unit == CENTURIES || unit == MILLENNIA || unit == ERAS;
        }
        return unit != null && unit.isSupportedBy(this);
    }

    @Override
    public Temporal with(TemporalAdjuster adjuster) {
        return adjuster.adjustInto(this);
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case YEAR_OF_ERA: return ChineseYear.of(sunYear < 1 ? 1 - newValue : newValue);
                case YEAR: return ChineseYear.of(newValue);
                case ERA: return (getLong(ERA) == newValue ? this : ChineseYear.of(1 - sunYear));
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.adjustInto(this, newValue);
    }

    @Override
    public Temporal plus(TemporalAmount amount) {
        return amount.addTo(this);
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            switch ((ChronoUnit) unit) {
                case YEARS: return plusYears(amountToAdd);
                case DECADES: return plusYears(Math.multiplyExact(amountToAdd, 10));
                case CENTURIES: return plusYears(Math.multiplyExact(amountToAdd, 100));
                case MILLENNIA: return plusYears(Math.multiplyExact(amountToAdd, 1000));
                case ERAS: return with(ERA, Math.addExact(getLong(ERA), amountToAdd));
            }
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
        return unit.addTo(this, amountToAdd);
    }

    public ChineseYear plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        return of(YEAR.checkValidIntValue(sunYear + yearsToAdd));  // overflow safe
    }

    public static ChineseYear of(int isoYear) {
        YEAR.checkValidValue(isoYear);
        return new ChineseYear(isoYear);
    }

    public static ChineseYear of(long isoYear) {
        return new ChineseYear(isoYear);
    }

    @Override
    public Temporal minus(TemporalAmount amount) {
        return amount.subtractFrom(this);
    }

    @Override
    public Temporal minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        ChineseYear end = ChineseYear.from(endExclusive);
        if (unit instanceof ChronoUnit) {
            long yearsUntil = end.sunYear - sunYear;  // no overflow
            switch ((ChronoUnit) unit) {
                case YEARS: return yearsUntil;
                case DECADES: return yearsUntil / 10;
                case CENTURIES: return yearsUntil / 100;
                case MILLENNIA: return yearsUntil / 1000;
                case ERAS: return end.getLong(ERA) - getLong(ERA);
            }
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
        return unit.between(this, end);
    }

    public static ChineseYear from(TemporalAccessor temporal) {
        if (temporal instanceof ChineseYear) {
            return (ChineseYear) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            if (IsoChronology.INSTANCE.equals(Chronology.from(temporal)) == false) {
                temporal = LocalDate.from(temporal);
            }
            return of(temporal.get(YEAR));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain Year from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return field == YEAR || field == YEAR_OF_ERA || field == ERA;
        }
        return field != null && field.isSupportedBy(this);
    }

    @Override
    public ValueRange range(TemporalField field) {
        if (field == YEAR_OF_ERA) {
            return (sunYear <= 0 ? ValueRange.of(1, Year.MAX_VALUE + 1) : ValueRange.of(1, Year.MAX_VALUE));
        }
        return Temporal.super.range(field);
    }

    @Override
    public int get(TemporalField field) {
        return range(field).checkValidIntValue(getLong(field), field);
    }

    @Override
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case YEAR_OF_ERA: return (sunYear < 1 ? 1 - sunYear : sunYear);
                case YEAR: return sunYear;
                case ERA: return (sunYear < 1 ? 0 : 1);
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    @Override
    public <R> R query(TemporalQuery<R> query) {
        return toYear().query(query);
    }

    @Override
    public String toString() {
        return "{" +
                "\"sunYear\":" + sunYear +
                ",\"year\":\"" + year + '\"' +
                ",\"tiangan\":\"" + tiangan + '\"' +
                ",\"dizhi\":\"" + dizhi + '\"' +
                ",\"shengxiao\":\"" + shengxiao + '\"' +
                '}';
    }

}
