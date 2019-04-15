package com.truthbean.debbie.core.data.transformer.text;

import com.truthbean.debbie.core.data.transformer.DataTransformer;
import com.truthbean.debbie.core.util.JacksonUtils;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 21:30
 */
public class XmlTransformer<T> implements DataTransformer<T, String> {

    private Class<T> clazz;
    public XmlTransformer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String transform(T original) {
        return JacksonUtils.toXml(original);
    }

    @Override
    public T reverse(String transformer) {
        return JacksonUtils.xmlToBean(transformer, this.clazz);
    }
}