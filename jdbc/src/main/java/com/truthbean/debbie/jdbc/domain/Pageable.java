package com.truthbean.debbie.jdbc.domain;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pageable {

    int page() default 1;

    int size() default 20;
}
