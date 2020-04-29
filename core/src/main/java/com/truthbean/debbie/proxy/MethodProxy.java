package com.truthbean.debbie.proxy;

import java.lang.annotation.*;


/**
 * @author truthbean
 * @since 0.0.1
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodProxy {

    int order() default Short.MAX_VALUE;

    Class<? extends MethodProxyHandler> proxyHandler() default DefaultMethodProxyHandler.class;
}
