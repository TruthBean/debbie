package com.truthbean.debbie.check.proxy;

import com.truthbean.debbie.proxy.MethodProxy;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MethodProxy()
public @interface CustomMethodProxy {
}
