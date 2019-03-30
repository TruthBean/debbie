package com.truthbean.code.debbie.mvc.response.view;

import com.truthbean.code.debbie.mvc.response.AbstractResponseHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-04-08 11:04.
 */

public abstract class AbstractTemplateViewHandler <S, T> extends AbstractResponseHandler<S, T> {

    @Override
    public final S reverse(T o) {
        throw new UnsupportedOperationException();
    }
}
