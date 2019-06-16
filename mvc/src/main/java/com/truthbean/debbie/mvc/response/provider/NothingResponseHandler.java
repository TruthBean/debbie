package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class NothingResponseHandler extends AbstractRestResponseHandler<Void> {
    @Override
    public String transform(Void aVoid) {
        return null;
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.ANY.info();
    }
}
