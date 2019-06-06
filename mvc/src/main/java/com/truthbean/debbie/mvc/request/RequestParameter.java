package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.MediaType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-11 13:14
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParameter {
    /**
     * if type is BODY, name can be ""
     * @return name
     */
    String name() default "";

    RequestParameterType paramType() default RequestParameterType.MIX;

    String defaultValue() default "";

    boolean require() default true;

    /**
     * if type is BODY, requestType must be specific
     * @return request body media type
     */
    MediaType bodyType() default MediaType.ANY;
}
