package com.truthbean.debbie.jdbc.annotation;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlEntity {
    /**
     * table name
     * @return table name
     */
    String value() default "";

    /**
     * table name
     * @return table name
     */
    String table() default "";

    String engine() default "InnoDB";

    String charset() default "utf8";
}
