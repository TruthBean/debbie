package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ShortTransformer implements DataTransformer<Short, String> {
    @Override
    public String transform(Short aShort) {
        return aShort.toString();
    }

    @Override
    public Short reverse(String s) {
        return Short.parseShort(s);
    }
}
