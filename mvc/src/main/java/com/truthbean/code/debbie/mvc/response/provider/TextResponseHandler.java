package com.truthbean.code.debbie.mvc.response.provider;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019-02-23 17:42
 */
public class TextResponseHandler<S> extends AbstractRestResponseHandler<S> {
    @Override
    public String transform(S s) {
        if (s != null) {
            return s.toString();
        }
        return null;
    }
}
