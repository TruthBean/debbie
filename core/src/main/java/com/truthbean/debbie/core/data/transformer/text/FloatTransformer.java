package com.truthbean.debbie.core.data.transformer.text;

import com.truthbean.debbie.core.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class FloatTransformer implements DataTransformer<Float, String> {
    @Override
    public String transform(Float result) {
        return String.valueOf(result);
    }

    @Override
    public Float reverse(String value) {
        return Float.parseFloat(value);
    }
}
