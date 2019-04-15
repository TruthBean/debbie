package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.response.provider.ResponseHandlerProviderEnum;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 18:53
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Router {
    /**
     * same as path
     * @return path regex
     */
    String[] value() default "";

    /**
     * router path regex
     * @return path regex
     */
    String[] path() default "";

    HttpMethod method() default HttpMethod.ALL;

    /**
     * request type, same as Content-Type
     * @return mediaType
     */
    MediaType requestType() default MediaType.ANY;

    /**
     * does view has template, like jsp, freemarker et.
     * @return bool
     */
    boolean hasTemplate() default false;

    String templateSuffix() default "";

    String templatePrefix() default "";

    /**
     * response type
     * @return mediaType
     */
    MediaType responseType() default MediaType.ANY;
    ResponseHandlerProviderEnum handlerFilter() default ResponseHandlerProviderEnum.JSON_RESTFUL;
}
