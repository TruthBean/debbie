package com.truthbean.debbie.proxy;

import java.lang.reflect.Method;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractMethodExecutor {

    public AbstractMethodExecutor(Class<?> interfaceType, Method method, Object configuration) {
    }

    /**
     * execute method
     * @param object proxy
     * @param args method args
     * @return method result
     */
    protected abstract Object execute(Object object, Object[] args);
}
