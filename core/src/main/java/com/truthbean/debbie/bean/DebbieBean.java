package com.truthbean.debbie.bean;

import java.lang.annotation.*;

/**
 * @author truthbean
 * @since 0.0.1
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieBean {

    String name() default "";
}
