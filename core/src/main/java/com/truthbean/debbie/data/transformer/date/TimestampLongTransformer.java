/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.date;

import com.truthbean.debbie.data.transformer.DataTransformer;

import java.sql.Timestamp;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-16 23:27
 */
public class TimestampLongTransformer implements DataTransformer<Timestamp, Long> {
    @Override
    public Long transform(Timestamp timestamp) {
        if (timestamp == null) {
            return 0L;
        }
        return timestamp.getTime();
    }

    @Override
    public Timestamp reverse(Long aLong) {
        if (aLong == null)
            return new Timestamp(0L);
        return new Timestamp(aLong);
    }
}
