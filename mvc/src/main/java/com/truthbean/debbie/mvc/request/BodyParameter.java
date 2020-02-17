package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.MediaType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParameter {
    /**
     * can be ""
     * @return name
     */
    String name() default "";

    /**
     * same as name
     * @return name
     */
    String value() default "";

    String defaultValue() default "";

    boolean require() default true;

    MediaType type() default MediaType.ANY;
}
