package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.debbie.jdbc.entity.DateTimeEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author truthbean/Rogar·Q
 * @since 0.1.0
 * Created on 2020/7/11 14:30.
 */
@SqlRepository
public class DateTimeRepository extends CustomRepository<DateTimeEntity, Void> {

    public LocalDateTime localDateTime() {
        String sql = "select now() as now";
        return selectOne(sql, LocalDateTime.class);
    }

    public LocalDate localDate() {
        String sql = "select now() as now";
        return selectOne(sql, LocalDate.class);
    }

    public LocalTime localTime() {
        String sql = "select now() as now";
        return selectOne(sql, LocalTime.class);
    }

    public Instant instant() {
        String sql = "select now() as now";
        return selectOne(sql, Instant.class);
    }

    public Date date() {
        String sql = "select now() as now";
        return selectOne(sql, Date.class);
    }

    public java.sql.Date sqlDate() {
        String sql = "select now() as now";
        return selectOne(sql, java.sql.Date.class);
    }

    public java.sql.Time sqlTime() {
        String sql = "select now() as now";
        return selectOne(sql, java.sql.Time.class);
    }

    public java.sql.Timestamp timestamp() {
        String sql = "select now() as now";
        return selectOne(sql, java.sql.Timestamp.class);
    }

    public Calendar calendar() {
        String sql = "select now() as now";
        return selectOne(sql, Calendar.class);
    }

    public Long now() {
        String sql = "select now() as now";
        return selectOne(sql, Long.class);
    }

    public DateTimeEntity dateTimeEntity() {
        String sql = "select now() as now";
        return selectOne(sql, DateTimeEntity.class);
    }
}
