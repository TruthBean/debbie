/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-06 15:32.
 */
public interface JsonHelper {
    /**
     * bean、array、List、Map --&gt; json
     *
     * @param obj bean、array、List、Map
     * @return json string
     */
    String toJson(Object obj);

    /**
     * json string --&gt; bean、Map、List(array)
     *
     * @param json string
     * @param type   class of bean
     * @param <T>     class
     * @return obj
     */
    <T> T jsonToBean(String json, Class<T> type);

    /**
     * @param jsonInputStream json stream
     * @param clazz class of bean
     * @param <T> class
     * @return obj
     */
    <T> T jsonStreamToBean(InputStream jsonInputStream, Class<T> clazz);

    @SuppressWarnings("rawtypes")
    <T> Collection<T> jsonToCollectionBean(String body, Class<? extends Collection> setClass, Class<T> clazz);

    <T> List<T> jsonToListBean(String body, Class<T> clazz);

    <T> Set<T> jsonStreamToSetBean(InputStream stream, Class<T> clazz);

    <T> List<T> jsonStreamToListBean(InputStream stream, Class<T> clazz);
}
