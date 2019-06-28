package com.truthbean.debbie.data.transformer.numeric;

import com.truthbean.debbie.data.transformer.DataTransformer;

import java.math.BigDecimal;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class BigDecimalToLongTransformer implements DataTransformer<BigDecimal, Long> {

    @Override
    public Long transform(BigDecimal bigDecimal) {
        return bigDecimal.longValue();
    }

    @Override
    public BigDecimal reverse(Long aLong) {
        return new BigDecimal(aLong);
    }
}
