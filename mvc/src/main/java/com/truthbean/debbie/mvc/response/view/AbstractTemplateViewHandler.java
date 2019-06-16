package com.truthbean.debbie.mvc.response.view;

import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-04-08 11:04.
 */
public abstract class AbstractTemplateViewHandler<S, T> extends AbstractResponseContentHandler<S, T> {

    @Override
    public S reverse(T o) {
        throw new UnsupportedOperationException();
    }
}
