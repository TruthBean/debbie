package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.util.JacksonUtils;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:01.
 */
public class JsonResponseHandler<S> extends AbstractRestResponseHandler<S> {
    @Override
    public String transform(S original) {
        return JacksonUtils.toJson(original);
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.APPLICATION_JSON_UTF8.info();
    }
}
