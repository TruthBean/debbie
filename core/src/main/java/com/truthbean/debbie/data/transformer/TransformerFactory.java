package com.truthbean.debbie.data.transformer;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface TransformerFactory<O, T> {

    <S extends T> DataTransformer<O, S> getTransformer(Class<S> targetType);
}
