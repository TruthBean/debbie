/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.date;

import com.truthbean.transformer.DataTransformer;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-16 23:27
 */
public class TimestampLocalTimeTransformer implements DataTransformer<Timestamp, LocalTime> {
    @Override
    public LocalTime transform(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime().toLocalTime();
    }

    @Override
    public Timestamp reverse(LocalTime date) {
        if (date == null)
            return new Timestamp(0L);
        return new Timestamp(Time.valueOf(date).getTime());
    }
}
