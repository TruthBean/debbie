package com.truthbean.jackson;

import com.truthbean.Console;
import com.truthbean.debbie.jackson.util.JacksonUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/03/17 16:50.
 */
public class JsonTest {

    public static void main(String[] args) {
        TimeBean bean = new TimeBean();

        LocalTime time = LocalTime.now();
        bean.setTime(time);

        LocalDate date = LocalDate.now();
        bean.setDate(date);

        LocalDateTime dateTime = LocalDateTime.now();
        bean.setDateTime(dateTime);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        bean.setTimestamp(timestamp);

        String json = JacksonUtils.toJson(bean);
        Console.info(json);

        TimeBean timeBean = JacksonUtils.jsonToBean(json, TimeBean.class);
        Console.info(timeBean.toString());
    }
}
