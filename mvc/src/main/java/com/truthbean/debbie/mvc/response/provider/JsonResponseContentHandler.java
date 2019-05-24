package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.core.util.JacksonUtils;

/**
 * @author TruthBean
 * @since ${version}
 * Created on 2019/3/23 14:01.
 */
public class JsonResponseContentHandler<S> extends AbstractRestResponseContentHandler<S> {
    @Override
    public String transform(S original) {
        return JacksonUtils.toJson(original);
    }
}
