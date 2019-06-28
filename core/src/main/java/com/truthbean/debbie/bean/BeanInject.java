package com.truthbean.debbie.bean;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanInject {

    String name() default "";

    /**
     * same as name
     * @since 0.0.2
     * @return name
     */
    String value() default "";

    boolean require() default true;
}
