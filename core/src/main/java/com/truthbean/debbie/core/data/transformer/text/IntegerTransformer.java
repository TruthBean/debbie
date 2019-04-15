package com.truthbean.debbie.core.data.transformer.text;

import com.truthbean.debbie.core.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class IntegerTransformer implements DataTransformer<Integer, String> {
    @Override
    public String transform(Integer integer) {
        return String.valueOf(integer);
    }

    @Override
    public Integer reverse(String s) {
        return Integer.parseInt(s);
    }
}
