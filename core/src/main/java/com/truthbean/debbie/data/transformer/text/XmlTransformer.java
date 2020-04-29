package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.util.JacksonUtils;

import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 21:30
 */
public class XmlTransformer<T> implements DataTransformer<T, String> {

    private final Class<T> type;

    public XmlTransformer(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public XmlTransformer() {
        type = (Class<T>) Map.class;
    }

    @Override
    public String transform(T original) {
        return JacksonUtils.toXml(original);
    }

    @Override
    public T reverse(String transformer) {
        return JacksonUtils.xmlToBean(transformer, this.type);
    }
}