package com.truthbean.code.debbie.mvc.response.provider;

import com.truthbean.code.debbie.core.util.JacksonUtils;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-03 12:01
 */
public class XmlResponseHandler<S> extends AbstractRestResponseHandler<S> {

    @Override
    public String transform(S s) {
        return JacksonUtils.toXml(s);
    }
}
