/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;

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
