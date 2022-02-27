package com.truthbean.debbie.jackson.data;

import com.truthbean.debbie.data.XmlHelper;
import com.truthbean.debbie.data.serialize.jackson.JacksonXmlUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/04 09:21.
 */
public class JacksonXmlHelper implements XmlHelper {
    @Override
    public <T> T xmlToBean(String xml, Class<T> clazz) {
        return JacksonXmlUtils.xmlToBean(xml, clazz);
    }

    @Override
    public <T> Set<T> xmlToSetBean(String xml, Class<T> clazz) {
        return JacksonXmlUtils.xmlToSetBean(xml, clazz);
    }

    @Override
    public <T> List<T> xmlToListBean(String xml, Class<T> clazz) {
        return JacksonXmlUtils.xmlToListBean(xml, clazz);
    }

    @Override
    public <T> T xmlStreamToBean(InputStream stream, Class<T> clazz) {
        return JacksonXmlUtils.xmlStreamToBean(stream, clazz);
    }

    @Override
    public <T> Set<T> xmlStreamToSetBean(InputStream stream, Class<T> clazz) {
        return JacksonXmlUtils.xmlStreamToSetBean(stream, clazz);
    }

    @Override
    public <T> List<T> xmlStreamToListBean(InputStream stream, Class<T> clazz) {
        return JacksonXmlUtils.xmlStreamToListBean(stream, clazz);
    }
}
