package com.truthbean.debbie.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class BeanMethodProxy<B> {
    private B bean;

    private Collection<MethodProxyHandler> interceptors;

    public BeanMethodProxy(B b, Collection<MethodProxyHandler> interceptors) {
        this.bean = b;
        this.interceptors = interceptors;
    }

    public Object proxy(String methodName, Callable callable) throws Throwable {
        // before
        Map<MethodProxyHandler, Exception> beforeInvokeExceptions = new HashMap<>();
        for (MethodProxyHandler interceptor : interceptors) {
            try {
                interceptor.before();
            } catch (Exception e) {
                Throwable throwable = e.getCause();
                if (throwable == null) {
                    throwable = e;
                }
                LOGGER.error(interceptor.getClass().getName() + " invoke method(" + methodName + ") on before error. \n " + throwable.getMessage(), throwable);
                beforeInvokeExceptions.put(interceptor, e);
            }
        }
        if (!beforeInvokeExceptions.isEmpty()) {
            beforeInvokeExceptions.forEach((key, value) -> {
                interceptors.remove(key);
            });
        }

        if (interceptors.isEmpty()) {
            return null;
        }

        // invoke
        Throwable invokeException = null;
        Object invoke = null;
        try {
            callable.call();
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            if (throwable == null) {
                throwable = e;
            }
            LOGGER.error(" invoke method(" + methodName + ") error. \n", throwable);
            invokeException = throwable;
        }

        Map<MethodProxyHandler, Throwable> catchedExceptions = new HashMap<>();

        if (invokeException != null) {
            // do catch exception
            for (MethodProxyHandler interceptor : interceptors) {
                try {
                    interceptor.catchException(invokeException);
                } catch (Throwable e) {
                    catchedExceptions.put(interceptor, e);
                }
            }
        } else {
            // after
            Map<MethodProxyHandler, Throwable> afterInvokeExceptions = new HashMap<>();
            for (MethodProxyHandler interceptor : interceptors) {
                try {
                    interceptor.after();
                } catch (Exception e) {
                    Throwable throwable = e.getCause();
                    if (throwable == null) {
                        throwable = e;
                    }
                    LOGGER.error(interceptor.getClass().getName() + " invoke method(" + methodName + ") on after error. \n", throwable);
                    afterInvokeExceptions.put(interceptor, e);
                }
            }

            if (!afterInvokeExceptions.isEmpty()) {
                // do catch except
                afterInvokeExceptions.forEach((key, value) -> {
                    try {
                        key.catchException(value);
                    } catch (Throwable e) {
                        catchedExceptions.put(key, e);
                    }
                });
            }
        }

        // finally
        for (MethodProxyHandler proxyHandler : interceptors) {
            try {
                proxyHandler.finallyRun();
            } catch (Exception e) {
                Throwable throwable = e.getCause();
                if (throwable == null) {
                    throwable = e;
                }
                LOGGER.error(proxyHandler.getClass().getName() + " invoke method(" + methodName + ") on finally error. \n", throwable);
            }
        }

        if (invokeException != null) {
            throw invokeException;
        }

        if (!catchedExceptions.isEmpty()) {
            throw catchedExceptions.values().iterator().next();
        }

        return invoke;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanMethodProxy.class);
}
