/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.text.jackson;

import com.truthbean.debbie.data.serialize.jackson.JacksonXmlUtils;
import com.truthbean.transformer.DataTransformer;

import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 21:30
 */
public class XmlTransformer<T> implements DataTransformer<T, String> {

    private final Class<T> type;

    public XmlTransformer(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public XmlTransformer() {
        type = (Class<T>) Map.class;
    }

    @Override
    public String transform(T original) {
        return JacksonXmlUtils.toXml(original);
    }

    @Override
    public T reverse(String transformer) {
        return JacksonXmlUtils.xmlToBean(transformer, this.type);
    }
}