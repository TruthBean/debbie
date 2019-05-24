package com.truthbean.debbie.core.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultMethodProxyHandler implements MethodProxyHandler<MethodProxy> {

    private String methodName;

    DefaultMethodProxyHandler(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public void before() {
        LOGGER.debug("runing before method(" + methodName + ") invoke ..");
    }

    @Override
    public void after() {
        LOGGER.debug("runing after method(" + methodName + ") invoke ..");
    }

    @Override
    public void whenExceptionCatched(Throwable e) throws Throwable {
        LOGGER.debug("runing when method(" + methodName + ") invoke throw exception and cached ..", e);
        if (e != null) {
            throw e;
        }
    }

    @Override
    public void finallyRun() {
        LOGGER.debug("runing when method(" + methodName + ") invoke throw exception and run to finally ..");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMethodProxyHandler.class);
}
