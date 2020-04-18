package com.truthbean.debbie.event;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventMethodListener {

    boolean async() default false;
}
