package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;

import java.math.BigDecimal;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class BigDecimalTransformer implements DataTransformer<BigDecimal, String> {

    @Override
    public String transform(BigDecimal bigDecimal) {
        // If the specified value is null, return a zero-length String
        if (bigDecimal == null) {
            return "";
        }

        return bigDecimal.toPlainString();
    }

    @Override
    public BigDecimal reverse(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        return new BigDecimal(value);
    }
}