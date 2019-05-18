package com.truthbean.debbie.core.data.transformer.text;

import com.truthbean.debbie.core.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018/3/16 16:07.
 */
public class DefaultTextTransformer implements DataTransformer<Object, String> {
    @Override
    public String transform(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    @Override
    public Object reverse(String s) {
        return s;
    }
}
