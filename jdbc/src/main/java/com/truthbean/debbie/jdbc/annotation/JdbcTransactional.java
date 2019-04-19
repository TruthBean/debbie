package com.truthbean.debbie.jdbc.annotation;

import java.lang.annotation.*;

/**
 * jdbc connection transactional
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JdbcTransactional {

}