/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.data.transformer.collection.SetStringTransformer;
import com.truthbean.debbie.data.transformer.date.*;
import com.truthbean.debbie.data.transformer.jdbc.BlobToByteArrayTransformer;
import com.truthbean.debbie.data.transformer.jdbc.BlobToStringTransformer;
import com.truthbean.debbie.data.transformer.numeric.BigDecimalToLongTransformer;
import com.truthbean.debbie.data.transformer.numeric.IntegerToBooleanTransformer;
import com.truthbean.debbie.data.transformer.numeric.LongToIntegerTransformer;
import com.truthbean.debbie.data.transformer.text.*;

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
public class DataTransformerRegister {

    private final BeanInitialization beanInitialization;

    public DataTransformerRegister(BeanInitialization beanInitialization) {
        this.beanInitialization = beanInitialization;
    }

    public void registerTransformer() {
        beanInitialization.registerDataTransformer(new DefaultTimeTransformer(), Date.class, String.class);
        beanInitialization.registerDataTransformer(new TimestampLongTransformer(), Timestamp.class, Long.class);
        beanInitialization.registerDataTransformer(new TimestampCalendarTransformer(), Timestamp.class, Calendar.class);
        beanInitialization.registerDataTransformer(new TimestampInstantTransformer(), Timestamp.class, Instant.class);
        beanInitialization.registerDataTransformer(new TimestampLocalDateTimeTransformer(), Timestamp.class, LocalDateTime.class);
        beanInitialization.registerDataTransformer(new TimestampLocalDateTransformer(), Timestamp.class, LocalDate.class);
        beanInitialization.registerDataTransformer(new TimestampLocalTimeTransformer(), Timestamp.class, LocalTime.class);
        beanInitialization.registerDataTransformer(new TimestampDateTransformer(), Timestamp.class, Date.class);
        beanInitialization.registerDataTransformer(new TimestampSqlDateTransformer(), Timestamp.class, java.sql.Date.class);
        beanInitialization.registerDataTransformer(new TimestampSqlTimeTransformer(), Timestamp.class, Time.class);
        try {
            Class<?> jsonNode = getClass().getClassLoader().loadClass("com.fasterxml.jackson.databind.JsonNode");
            beanInitialization.registerDataTransformer(new JsonNodeTransformer(), jsonNode, String.class);
        } catch (NoClassDefFoundError | ClassNotFoundException ignored) {
        }
        beanInitialization.registerDataTransformer(new UrlTransformer(), URL.class, String.class);

        beanInitialization.registerDataTransformer(new BigDecimalTransformer(), BigDecimal.class, String.class);
        beanInitialization.registerDataTransformer(new BigIntegerTransformer(), BigInteger.class, String.class);
        beanInitialization.registerDataTransformer(new BooleanTransformer(), Boolean.class, String.class);
        beanInitialization.registerDataTransformer(new FloatTransformer(), Float.class, String.class);
        beanInitialization.registerDataTransformer(new IntegerTransformer(), Integer.class, String.class);
        beanInitialization.registerDataTransformer(new LongTransformer(), Long.class, String.class);
        beanInitialization.registerDataTransformer(new ShortTransformer(), Short.class, String.class);

        beanInitialization.registerDataTransformer(new IntegerToBooleanTransformer(), Integer.class, Boolean.class);
        beanInitialization.registerDataTransformer(new LongToIntegerTransformer(), Long.class, Integer.class);
        beanInitialization.registerDataTransformer(new BigDecimalToLongTransformer(), BigDecimal.class, Long.class);

        beanInitialization.registerDataTransformer(new SetStringTransformer());
        beanInitialization.registerDataTransformer(new ClassTransformer(), Class.class, String.class);

        beanInitialization.registerDataTransformer(new BlobToStringTransformer(), Blob.class, String.class);
        beanInitialization.registerDataTransformer(new BlobToByteArrayTransformer(), Blob.class, byte[].class);
    }
}
