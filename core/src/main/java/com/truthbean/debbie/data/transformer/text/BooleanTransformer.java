package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/18 10:44.
 */
public class BooleanTransformer implements DataTransformer<Boolean, String> {
    @Override
    public String transform(Boolean bool) {
        return bool.toString();
    }

    @Override
    public Boolean reverse(String s) {
        return Boolean.valueOf(s);
    }
}
