package com.truthbean.code.debbie.mvc.request;

import com.truthbean.code.debbie.core.io.MediaType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-11 13:14
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    /**
     * if type is BODY, name can be ""
     * @return name
     */
    String name() default "";

    RequestParamType type() default RequestParamType.MIX;

    String defaultValue() default "";

    boolean require() default true;

    /**
     * if type is BODY, requestType must be specific
     * @return request body media type
     */
    MediaType requestType() default MediaType.ANY;
}
