package com.truthbean.debbie.core.proxy;

import com.truthbean.debbie.core.bean.BeanFactoryHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface MethodProxyHandler<A extends Annotation> extends Comparable {

    default void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {

    }

    /**
     * when method has more Annotation
     * call handler order by it
     * @return order
     */
    int getOrder();

    void setOrder(int order);

    default boolean exclusive() {
        return false;
    }

    default void setMethodAnnotation(A methodAnnotation) {
    }

    default void setClassAnnotation(A classAnnotation) {
    }

    default void setMethod(Method method) {
    }

    void before();

    void after();

    void whenExceptionCatched(Throwable e) throws Throwable;

    void finallyRun();

    @Override
    default int compareTo(Object o) {
        if (o instanceof MethodProxyHandler) {
            int order = ((MethodProxyHandler) o).getOrder();
            return Integer.compare(order, getOrder());
        }
        return 0;
    }
}
