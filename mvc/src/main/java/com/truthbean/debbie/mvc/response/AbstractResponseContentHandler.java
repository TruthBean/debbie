/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
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
 * Base class for response content handlers that transform a source
 * object {@code S} into a response value {@code V} via
 * {@link DataTransformer#transform}.
 *
 * @param <S> the source type
 * @param <V> the response value type
 * @author truthbean
 * @since 0.0.1
 * Created on 2018-02-19 15:15
 */
public abstract class AbstractResponseContentHandler<S, V> implements DataTransformer<S, V> {

    /** the application context */
    private ApplicationContext applicationContext;

    /** Sets the application context. */
    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /** Returns the response media type produced by this handler. */
    public abstract MediaTypeInfo getResponseType();

    /** Returns the application context. */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /** Reverse transformation is not supported by default. */
    @Override
    public S reverse(V ingored) {
        throw new UnsupportedOperationException();
    }

    /**
     * Transforms the source object and sets it as the response content.
     *
     * @param response the router response
     * @param s        the source object
     */
    public void handleResponse(RouterResponse response, S s) {
        V v = transform(s);
        response.setContent(v);
    }
}
