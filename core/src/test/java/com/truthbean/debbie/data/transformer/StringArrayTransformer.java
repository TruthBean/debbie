package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.util.StringUtils;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-18 18:04
 */
@Transformer
public class StringArrayTransformer implements DataTransformer<String[], String> {
    @Override
    public String transform(String[] strings) {
        return StringUtils.joining(strings, ",");
    }

    @Override
    public String[] reverse(String s) {
        return s.split(",");
    }
}
