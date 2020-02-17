package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.bean.*;

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

    @BeanAliceForName
    @BeanAliceFor(name = "name")
    String name() default "";

    @BeanAliceForType
    @BeanAliceFor(name = "type")
    BeanType type() default BeanType.SINGLETON;

    String[] value() default {};

    String[] urlPatterns() default {};

    int order() default 0;
}
