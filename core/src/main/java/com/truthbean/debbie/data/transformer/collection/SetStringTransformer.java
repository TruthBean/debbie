package com.truthbean.debbie.data.transformer.collection;

import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class SetStringTransformer implements DataTransformer<Set<String>, String> {
    @Override
    public String transform(Set<String> strings) {
        return StringUtils.joining(strings, ",");
    }

    @Override
    public Set<String> reverse(String s) {
        if (s != null) {
            String[] split = s.split(",");
            return new HashSet<>(Arrays.asList(split));
        }
        return null;
    }
}
