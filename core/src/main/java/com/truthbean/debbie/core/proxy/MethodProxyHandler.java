package com.truthbean.debbie.core.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface MethodProxyHandler<A extends Annotation> {

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
}
