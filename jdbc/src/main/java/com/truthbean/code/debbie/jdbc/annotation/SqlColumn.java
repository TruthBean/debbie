package com.truthbean.code.debbie.jdbc.annotation;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 15:47.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlColumn {
    /**
     * is id
     * @return whether is id
     */
    boolean id() default false;

    /**
     * @return column name
     */
    String name() default "";
}
