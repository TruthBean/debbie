package com.truthbean.debbie.bean;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieBean {

    String name() default "";
}
