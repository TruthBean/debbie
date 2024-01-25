/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.transformer.DataTransformer;
import com.truthbean.debbie.io.MediaTypeInfo;

/**
 * @author truthbean
 * @since 0.0.1
 * Created on 2018-02-19 15:15
 */
public abstract class AbstractResponseContentHandler<S, V> implements DataTransformer<S, V> {

    private ApplicationContext applicationContext;

    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public abstract MediaTypeInfo getResponseType();

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public S reverse(V ingored) {
        throw new UnsupportedOperationException();
    }

    public void handleResponse(RouterResponse response, S s) {
        V v = transform(s);
        response.setContent(v);
    }
}
