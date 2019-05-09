package com.truthbean.debbie.core.proxy;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodProxy {

    Class<? extends MethodProxyHandler> proxyHandler();
}
