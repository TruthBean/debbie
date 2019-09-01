package com.truthbean.debbie.task;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieTask {

    int order() default Integer.MAX_VALUE;

    boolean async() default false;
}
