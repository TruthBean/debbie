package com.truthbean.debbie.proxy;

import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class MethodProxyHandlerHandler {
    private Collection<MethodProxyHandler> interceptors;

    private Logger logger;

    public MethodProxyHandlerHandler() {
    }

    public void setInterceptors(Collection<MethodProxyHandler> interceptors) {
        this.interceptors = interceptors;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public <T> T proxy(String methodName, Callable<T> noInterceptorsCallback, Callable<T> callback) throws Throwable {
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
                logger.error(interceptor.getClass().getName() + " invoke method(" + methodName + ") on before error. \n " + throwable.getMessage(), throwable);
                beforeInvokeExceptions.put(interceptor, e);
            }
        }
        if (!beforeInvokeExceptions.isEmpty()) {
            beforeInvokeExceptions.forEach((key, value) -> {
                interceptors.remove(key);
            });
        }

        if (interceptors.isEmpty()) {
            return noInterceptorsCallback.call();
        }

        // invoke
        Throwable invokeException = null;
        T invoke = null;
        try {
            invoke = callback.call();
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            if (throwable == null) {
                throwable = e;
            }
            logger.error(" invoke method(" + methodName + ") error. \n", throwable);
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
                    logger.error(interceptor.getClass().getName() + " invoke method(" + methodName + ") on after error. \n", throwable);
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
                logger.error(proxyHandler.getClass().getName() + " invoke method(" + methodName + ") on finally error. \n", throwable);
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
}
