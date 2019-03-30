package com.truthbean.code.debbie.core.data.transformer;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 13:51
 */
public interface DataTransformer<Original, Transformer> {

    /**
     * transform data
     * @param original original data
     * @return data after transformed
     */
    Transformer transform(Original original);

    /**
     * transformer to original
     * @param transformer transformer value
     * @return original value
     */
    Original reverse(Transformer transformer);
}