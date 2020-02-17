package com.truthbean.debbie.task;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {
}
