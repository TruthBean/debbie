/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.core;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.transformer.ClassTransformer;
import com.truthbean.transformer.DataTransformerCenter;
import com.truthbean.transformer.collection.ListStringTransformer;
import com.truthbean.transformer.collection.SetStringTransformer;
import com.truthbean.debbie.data.transformer.date.DateSqlDateTransformer;
import com.truthbean.debbie.data.transformer.date.DateSqlTimeTransformer;
import com.truthbean.transformer.date.DefaultTimeTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampCalendarTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampDateTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampInstantTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampLocalDateTimeTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampLocalDateTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampLocalTimeTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampLongTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampSqlDateTransformer;
import com.truthbean.debbie.data.transformer.date.TimestampSqlTimeTransformer;
import com.truthbean.debbie.data.transformer.jdbc.BlobToByteArrayTransformer;
import com.truthbean.debbie.data.transformer.jdbc.BlobToStringTransformer;
import com.truthbean.transformer.numeric.BigDecimalToIntegerTransformer;
import com.truthbean.transformer.numeric.BigDecimalToLongTransformer;
import com.truthbean.transformer.numeric.IntegerToBooleanTransformer;
import com.truthbean.transformer.numeric.LongToBooleanTransformer;
import com.truthbean.transformer.numeric.LongToIntegerTransformer;
import com.truthbean.transformer.text.BigDecimalTransformer;
import com.truthbean.transformer.text.BigIntegerTransformer;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.DoubleArrayTransformer;
import com.truthbean.transformer.text.FloatArrayTransformer;
import com.truthbean.transformer.text.FloatTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.debbie.data.transformer.text.JsonNodeTransformer;
import com.truthbean.transformer.text.LongTransformer;
import com.truthbean.transformer.text.ShortTransformer;
import com.truthbean.debbie.data.transformer.text.UrlTransformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author TruthBean
 * @since 0.1.0
 * Created on 2020/7/11 17:40.
 */
class DataTransformerRegister {

    DataTransformerRegister(BeanInfoManager beanInfoManage) {
    }

    public void registerTransformer() {
        DataTransformerCenter.register(DefaultTimeTransformer.getInstance(), Date.class, String.class);
        DataTransformerCenter.register(new TimestampLongTransformer(), Timestamp.class, Long.class);
        DataTransformerCenter.register(new TimestampCalendarTransformer(), Timestamp.class, Calendar.class);
        DataTransformerCenter.register(new TimestampInstantTransformer(), Timestamp.class, Instant.class);
        DataTransformerCenter.register(new TimestampLocalDateTimeTransformer(), Timestamp.class, LocalDateTime.class);
        DataTransformerCenter.register(new TimestampLocalDateTransformer(), Timestamp.class, LocalDate.class);
        DataTransformerCenter.register(new TimestampLocalTimeTransformer(), Timestamp.class, LocalTime.class);
        DataTransformerCenter.register(new TimestampDateTransformer(), Timestamp.class, Date.class);
        DataTransformerCenter.register(new TimestampSqlDateTransformer(), Timestamp.class, java.sql.Date.class);
        DataTransformerCenter.register(new TimestampSqlTimeTransformer(), Timestamp.class, Time.class);
        DataTransformerCenter.register(new DateSqlDateTransformer(), Date.class, java.sql.Date.class);
        DataTransformerCenter.register(new DateSqlTimeTransformer(), Date.class, Time.class);
        try {
            Class<?> jsonNode = getClass().getClassLoader().loadClass("com.fasterxml.jackson.databind.JsonNode");
            DataTransformerCenter.register(new JsonNodeTransformer(), jsonNode, String.class);
        } catch (NoClassDefFoundError | ClassNotFoundException ignored) {
        }
        DataTransformerCenter.register(new UrlTransformer(), URL.class, String.class);

        DataTransformerCenter.register(BigDecimalTransformer.INSTANCE, BigDecimal.class, String.class);
        DataTransformerCenter.register(BigIntegerTransformer.INSTANCE, BigInteger.class, String.class);
        DataTransformerCenter.register(BooleanTransformer.INSTANCE, Boolean.class, String.class);
        DataTransformerCenter.register(FloatTransformer.INSTANCE, Float.class, String.class);
        DataTransformerCenter.register(IntegerTransformer.INSTANCE, Integer.class, String.class);
        DataTransformerCenter.register(LongTransformer.INSTANCE, Long.class, String.class);
        DataTransformerCenter.register(ShortTransformer.INSTANCE, Short.class, String.class);
        DataTransformerCenter.register(FloatArrayTransformer.getInstance(), float[].class, String.class);
        DataTransformerCenter.register(DoubleArrayTransformer.getInstance(), double[].class, String.class);

        DataTransformerCenter.register(IntegerToBooleanTransformer.getInstance(), Integer.class, Boolean.class);
        DataTransformerCenter.register(LongToIntegerTransformer.getInstance(), Long.class, Integer.class);
        DataTransformerCenter.register(LongToBooleanTransformer.getInstance(), Long.class, Boolean.class);
        DataTransformerCenter.register(BigDecimalToLongTransformer.getInstance(), BigDecimal.class, Long.class);
        DataTransformerCenter.register(BigDecimalToIntegerTransformer.getInstance(), BigDecimal.class, Integer.class);

        DataTransformerCenter.register(SetStringTransformer.getInstance());
        DataTransformerCenter.register(ListStringTransformer.getInstance());
        DataTransformerCenter.register(ClassTransformer.INSTANCE, Class.class, String.class);

        DataTransformerCenter.register(new BlobToStringTransformer(), Blob.class, String.class);
        DataTransformerCenter.register(new BlobToByteArrayTransformer(), Blob.class, byte[].class);
    }
}
