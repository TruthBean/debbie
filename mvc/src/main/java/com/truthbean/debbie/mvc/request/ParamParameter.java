package com.truthbean.debbie.mvc.request;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamParameter {

    String name() default "";

    /**
     * same as name
     * @return name
     */
    String value() default "";

    String defaultValue() default "";

    boolean require() default true;
}
