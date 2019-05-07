package com.truthbean.debbie.mvc.filter;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filter {

    String name() default "";

    String[] value() default {};

    String[] urlRegex() default {};

    int order() default 0;
}
