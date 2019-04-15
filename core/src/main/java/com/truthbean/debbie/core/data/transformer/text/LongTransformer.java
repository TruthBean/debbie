package com.truthbean.debbie.core.data.transformer.text;

import com.truthbean.debbie.core.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class LongTransformer implements DataTransformer<Long, String> {
    @Override
    public String transform(Long aLong) {
        return aLong.toString();
    }

    @Override
    public Long reverse(String s) {
        return Long.parseLong(s);
    }
}
