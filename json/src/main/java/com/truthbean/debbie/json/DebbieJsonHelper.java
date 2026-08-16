/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.json;

import com.truthbean.debbie.data.JsonHelper;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Pure-Java {@link JsonHelper} implementation backed by the self-contained
 * {@link JsonUtils} parser/serializer (no third-party dependencies).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class DebbieJsonHelper implements JsonHelper {

    @Override
    public String toJson(Object obj) {
        return JsonUtils.toJson(obj);
    }

    @Override
    public <T> T jsonToBean(String json, Class<T> type) {
        return JsonUtils.fromJson(json, type);
    }

    @Override
    public <T> T jsonStreamToBean(InputStream jsonInputStream, Class<T> clazz) {
        return JsonUtils.fromJson(jsonInputStream, clazz);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> setClass, Class<T> clazz) {
        JsonElement element = JsonParser.parse(body);
        return (Collection<T>) JsonMapper.fromJsonElement(element, setClass, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> jsonToListBean(String body, Class<T> clazz) {
        JsonElement element = JsonParser.parse(body);
        return (List<T>) JsonMapper.fromJsonElement(element, List.class, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz) {
        JsonElement element = JsonUtils.parse(stream);
        return (Set<T>) JsonMapper.fromJsonElement(element, Set.class, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz) {
        JsonElement element = JsonUtils.parse(stream);
        return (List<T>) JsonMapper.fromJsonElement(element, List.class, clazz);
    }
}