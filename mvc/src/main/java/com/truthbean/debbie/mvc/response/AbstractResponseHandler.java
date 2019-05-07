package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.core.data.transformer.DataTransformer;

/**
 * @author truthbean
 * @since 0.0.1
 * Created on 2018-02-19 15:15
 */
public abstract class AbstractResponseHandler<S, V> implements DataTransformer<S, V> {

    public final S reverse(String ingored) {
        throw new UnsupportedOperationException();
    }
}
