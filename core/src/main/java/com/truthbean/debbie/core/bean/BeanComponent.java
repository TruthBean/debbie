package com.truthbean.debbie.core.bean;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanComponent {

    String value() default "";

    BeanInjectType type() default BeanInjectType.NO_LIMIT;

}
