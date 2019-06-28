package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface Filter {

    String name() default "";

    BeanType type() default BeanType.SINGLETON;

    String[] value() default {};

    String[] urlPatterns() default {};

    int order() default 0;
}
