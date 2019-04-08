package com.truthbean.code.debbie.core.data.transformer.text;

import com.truthbean.code.debbie.core.data.transformer.DataTransformer;

import java.math.BigInteger;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class BigIntegerTransformer implements DataTransformer<BigInteger, String> {
    @Override
    public String transform(BigInteger bigInteger) {
        // If the specified value is null, return a zero-length String
        if (bigInteger == null) {
            return "";
        }

        return bigInteger.toString();
    }

    @Override
    public BigInteger reverse(String result) {
        // If the specified value is null or zero-length, return null
        if (result == null) {
            return null;
        }

        result = result.trim();

        if (result.length() < 1) {
            return null;
        }

        return new BigInteger(result);
    }
}
