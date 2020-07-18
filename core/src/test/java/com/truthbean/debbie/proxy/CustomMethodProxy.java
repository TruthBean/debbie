package com.truthbean.debbie.proxy;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MethodProxy(proxyHandler = DefaultMethodProxyHandler.class)
public @interface CustomMethodProxy {
}
