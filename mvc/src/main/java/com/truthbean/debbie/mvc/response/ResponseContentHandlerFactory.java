package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.reflection.ReflectionHelper;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class ResponseContentHandlerFactory {

    private final BeanFactoryHandler beanFactoryHandler;

    public ResponseContentHandlerFactory(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    public <H extends AbstractResponseContentHandler> H factory(Class<H> handlerClass) {
        H handler = ReflectionHelper.newInstance(handlerClass);
        handler.setBeanFactoryHandler(beanFactoryHandler);
        return handler;
    }
}
