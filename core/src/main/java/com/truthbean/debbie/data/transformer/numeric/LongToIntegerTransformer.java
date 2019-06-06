package com.truthbean.debbie.data.transformer.numeric;

import com.truthbean.debbie.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class LongToIntegerTransformer implements DataTransformer<Long, Integer> {
    @Override
    public Integer transform(Long aLong) {
        return aLong != null ? aLong.intValue() : 0;
    }

    @Override
    public Long reverse(Integer integer) {
        return integer != null ? integer.longValue() : 0L;
    }
}
