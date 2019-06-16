package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.util.JacksonUtils;

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

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.APPLICATION_XML_UTF8.info();
    }
}
