package com.truthbean.code.debbie.core.data.transformer.text;

import com.truthbean.code.debbie.core.data.transformer.DataTransformer;
import com.truthbean.code.debbie.core.util.JacksonUtils;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 13:50.
 */
public class JsonTransformer<T> implements DataTransformer<T, String> {

    private Class<T> clazz;
    public JsonTransformer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String transform(T original) {
        return JacksonUtils.toJson(original);
    }

    @Override
    public T reverse(String transformer) {
        return JacksonUtils.jsonToBean(transformer, this.clazz);
    }
}
