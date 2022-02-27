package com.truthbean.debbie.jackson.data;

import com.truthbean.debbie.data.JsonHelper;
import com.truthbean.debbie.data.serialize.jackson.JacksonJsonUtils;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/04 19:15.
 */
public class JacksonJsonHelper implements JsonHelper {
    @Override
    public String toJson(Object obj) {
        return JacksonJsonUtils.toJson(obj);
    }

    @Override
    public <T> T jsonToBean(String json, Class<T> type) {
        return JacksonJsonUtils.jsonToBean(json, type);
    }

    @Override
    public <T> T jsonStreamToBean(InputStream jsonInputStream, Class<T> clazz) {
        return JacksonJsonUtils.jsonStreamToBean(jsonInputStream, clazz);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> setClass, Class<T> clazz) {
        return JacksonJsonUtils.jsonToCollectionBean(body, setClass, clazz);
    }

    @Override
    public <T> List<T> jsonToListBean(String body, Class<T> clazz) {
        return JacksonJsonUtils.jsonToListBean(body, clazz);
    }

    @Override
    public <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz) {
        return JacksonJsonUtils.jsonStreamToSetBean(stream, clazz);
    }

    @Override
    public <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz) {
        return JacksonJsonUtils.jsonStreamToListBean(stream, clazz);
    }
}
