package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.bean.*;

import java.lang.annotation.*;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface SqlRepository {

    @BeanAliceForValue
    @BeanAliceFor(name = "value")
    String value() default "";

    @BeanAliceForType
    @BeanAliceFor(name = "type")
    BeanType type() default BeanType.NO_LIMIT;
}
