package com.truthbean.debbie.data;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/04 19:09.
 */
public interface XmlHelper {
    <T> T xmlToBean(String xml, Class<T> clazz);

    <T> Set<T> xmlToSetBean(String xml, Class<T> clazz);

    <T> List<T> xmlToListBean(String xml, Class<T> clazz);

    <T> T xmlStreamToBean(InputStream stream, Class<T> clazz);

    <T> Set<T> xmlStreamToSetBean(InputStream stream, Class<T> clazz);

    <T> List<T> xmlStreamToListBean(InputStream stream, Class<T> clazz);
}
