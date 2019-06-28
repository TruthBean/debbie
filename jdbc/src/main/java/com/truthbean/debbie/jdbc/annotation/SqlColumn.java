package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.jdbc.column.PrimaryKeyType;

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
    PrimaryKeyType primaryKey() default PrimaryKeyType.NONE;

    /**
     * @return column name
     */
    String name() default "";

    boolean nullable() default true;

    boolean unique() default false;

    int charMaxLength() default 64;

    String defaultValue() default "";

    String comment() default "";

    /**
     * TODO
     * column definition
     */
    String columnDefinition() default "";
}
