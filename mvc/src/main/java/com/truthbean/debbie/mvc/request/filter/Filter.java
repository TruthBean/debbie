package com.truthbean.debbie.mvc.request.filter;

import com.truthbean.debbie.core.bean.BeanComponent;
import com.truthbean.debbie.core.bean.BeanInjectType;

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

    BeanInjectType type() default BeanInjectType.SINGLETON;

    String[] value() default {};

    String[] urlPatterns() default {};

    int order() default 0;
}
