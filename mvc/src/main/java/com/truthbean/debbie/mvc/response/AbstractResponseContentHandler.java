package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.io.MediaTypeInfo;

/**
 * @author truthbean
 * @since 0.0.1
 * Created on 2018-02-19 15:15
 */
public abstract class AbstractResponseContentHandler<S, V> implements DataTransformer<S, V> {

    private BeanFactoryHandler beanFactoryHandler;

    void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    public abstract MediaTypeInfo getResponseType();

    public BeanFactoryHandler getBeanFactoryHandler() {
        return beanFactoryHandler;
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
