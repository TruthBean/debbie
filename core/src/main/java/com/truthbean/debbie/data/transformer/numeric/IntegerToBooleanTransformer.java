package com.truthbean.debbie.data.transformer.numeric;

import com.truthbean.debbie.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class IntegerToBooleanTransformer implements DataTransformer<Integer, Boolean> {
    @Override
    public Boolean transform(Integer integer) {
        return integer != 0;
    }

    @Override
    public Integer reverse(Boolean bool) {
        return bool ? 1 : 0;
    }
}
