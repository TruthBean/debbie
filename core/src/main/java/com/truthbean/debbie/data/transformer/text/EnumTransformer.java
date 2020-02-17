package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class EnumTransformer<T extends Enum<T>> implements DataTransformer<T, String> {

    private Class<T> type;

    public void setType(Class<T> type) {
        this.type = type;
    }

    @Override
    public String transform(T t) {
        return t.name();
    }

    @Override
    public T reverse(String s) {
        return Enum.valueOf(this.type, s);
    }
}
