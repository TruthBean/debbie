/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface PatchRouter {
    /**
     * router bean name
     * @return router bean name
     */
    String name() default "";

    /**
     * router always be singleton
     * @return BeanType
     */
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