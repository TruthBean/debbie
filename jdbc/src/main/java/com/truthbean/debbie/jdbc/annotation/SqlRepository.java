package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.BeanComponent;

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

    String value() default "";

    BeanType type() default BeanType.NO_LIMIT;
}
