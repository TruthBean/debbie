/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
