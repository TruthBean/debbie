/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import com.truthbean.Logger;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class MethodProxyHandlerHandler {
    private final Collection<MethodProxyHandler<? extends Annotation>> interceptors;

    private final Logger logger;
    public final Object reference = new Object();

    public MethodProxyHandlerHandler(Logger logger) {
        this.interceptors = new CopyOnWriteArraySet<>();
        this.logger = logger;
    }

    public void setInterceptors(Collection<MethodProxyHandler<? extends Annotation>> interceptors) {
        this.interceptors.clear();
        this.interceptors.addAll(interceptors);
    }

    public void addInterceptors(Collection<MethodProxyHandler<? extends Annotation>> interceptors) {
        this.interceptors.addAll(interceptors);
    }

    @SafeVarargs
    public final void addInterceptor(MethodProxyHandler<? extends Annotation>... interceptors) {
        if (interceptors != null && interceptors.length > 0)
            this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public boolean hasInterceptor() {
        return !this.interceptors.isEmpty();
    }

    public boolean sync() {
        for (MethodProxyHandler<? extends Annotation> interceptor : this.interceptors) {
            if (interceptor.sync()) {
                return true;
            }
        }
        return false;
    }

    public <T> T proxy(String methodName, Callable<T> noInterceptorsCallback, Callable<T> callback) throws Throwable {
        if (sync()) {
            synchronized (reference) {
                return doProxy(methodName, noInterceptorsCallback, callback);
            }
        } else {
            return doProxy(methodName, noInterceptorsCallback, callback);
        }
    }

    private <T> T doProxy(String methodName, Callable<T> noInterceptorsCallback, Callable<T> callback) throws Throwable {
        // before
        Map<MethodProxyHandler<? extends Annotation>, Exception> beforeInvokeExceptions = new HashMap<>();
        for (MethodProxyHandler<? extends Annotation> interceptor : interceptors) {
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
            beforeInvokeExceptions.forEach((key, value) -> interceptors.remove(key));
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

        Map<MethodProxyHandler<? extends Annotation>, Throwable> catchedExceptions = new HashMap<>();

        if (invokeException != null) {
            // do catch exception
            for (MethodProxyHandler<? extends Annotation> interceptor : interceptors) {
                try {
                    interceptor.catchException(invokeException);
                } catch (Throwable e) {
                    catchedExceptions.put(interceptor, e);
                }
            }
        } else {
            // after
            Map<MethodProxyHandler<? extends Annotation>, Throwable> afterInvokeExceptions = new HashMap<>();
            for (MethodProxyHandler<? extends Annotation> interceptor : interceptors) {
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
        for (MethodProxyHandler<? extends Annotation> proxyHandler : interceptors) {
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

    public <T> T proxy(MethodCallBack<T> callBack) {
        try {
            return proxy(callBack.getMethodName(), callBack, callBack);
        } catch (Throwable throwable) {
            throw new MethodProxyException(throwable);
        }
    }
}
