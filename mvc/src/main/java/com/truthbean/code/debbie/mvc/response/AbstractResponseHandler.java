package com.truthbean.code.debbie.mvc.response;

import com.truthbean.code.debbie.core.data.transformer.DataTransformer;

/**
 * @author truthbean
 * @since 0.0.1
 * Created on 2018-02-19 15:15
 */
public abstract class AbstractResponseHandler<S, V> implements DataTransformer<S, V> {

    public final S reverse(String s) {
        throw new UnsupportedOperationException();
    }
}
