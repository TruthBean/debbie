package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 18:53
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface Router {

    /**
     * router bean name
     * @return router bean name
     */
    @BeanAliceForName
    @BeanAliceFor(name = "name")
    String name() default "";

    /**
     * router always be singleton
     * @return BeanType
     */
    @BeanAliceForType
    @BeanAliceFor(name = "type")
    BeanType type() default BeanType.SINGLETON;

    /**
     * same as path
     * @return path regex
     */
    String[] value() default "";

    /**
     * router path patterns
     * @return path patterns
     */
    String[] urlPatterns() default "";

    HttpMethod[] method() default HttpMethod.ALL;

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
    Class<? extends AbstractResponseContentHandler> handlerClass() default NothingResponseHandler.class;
}
