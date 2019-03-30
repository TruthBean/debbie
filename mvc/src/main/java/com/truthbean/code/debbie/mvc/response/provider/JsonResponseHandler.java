package com.truthbean.code.debbie.mvc.response.provider;

import com.truthbean.code.debbie.core.util.JacksonUtils;

/**
 * @author TruthBean
 * @since ${version}
 * Created on 2019/3/23 14:01.
 */
public class JsonResponseHandler<S> extends AbstractRestResponseHandler<S> {
    @Override
    public String transform(S original) {
        return JacksonUtils.toJson(original);
    }
}
