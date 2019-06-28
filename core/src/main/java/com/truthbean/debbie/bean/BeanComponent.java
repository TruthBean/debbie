package com.truthbean.debbie.bean;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanComponent {

    /**
     * same as value
     * @since 0.0.2
     */
    String name() default "";

    String value() default "";

    BeanType type() default BeanType.NO_LIMIT;

    /**
     * lazy create bean, default true
     * @return boolean
     */
    boolean lazy() default true;

}
