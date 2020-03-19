package com.truthbean.debbie.data.transformer.date;

import com.truthbean.debbie.data.transformer.DataTransformer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 15:37.
 */
public abstract class AbstractTimeTransformer implements DataTransformer<Date, String> {
    /**
     * date time format, eg 'YYYY-MM-DD HH:mm:SS'
     */
    private String dateTimePattern;

    /**
     * dateFormat class
     */
    private DateTimeFormatter dateTimeFormatter;

    /**
     * set dateTimeFormat
     * @param dateTimeFormat dateTimeFormat
     */
    public void setFormat(String dateTimeFormat) {
        this.dateTimePattern = dateTimeFormat;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    }

    @Override
    public String transform(Date date) {
        if (date == null || dateTimePattern == null) {
            return null;
        }
        return dateTimeFormatter.format(Instant.ofEpochMilli(date.getTime()));
    }

    /**
     * string format datetime to milliseconds
     * @param string datetime
     * @return milliseconds
     */
    @Override
    public Date reverse(String string) {
        return new Date(dateTimeFormatter.parse(string, Instant::from).toEpochMilli());
    }
}
