package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.mvc.request.HttpMethod;
import com.truthbean.code.debbie.mvc.response.provider.ResponseHandlerProviderEnum;

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
    String value() default "";

    String pathRegex() default " ";

    HttpMethod method() default HttpMethod.ALL;

    MediaType responseType() default MediaType.ANY;

    /**
     * does view has template, like jsp, freemarker et.
     * @return bool
     */
    boolean hasTemplate() default false;
    String templateSuffix() default "";
    String templatePrefix() default "";

    ResponseHandlerProviderEnum handlerFilter() default ResponseHandlerProviderEnum.JSON_RESTFUL;
}
