package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

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

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.TEXT_PLAIN_UTF8.info();
    }
}
