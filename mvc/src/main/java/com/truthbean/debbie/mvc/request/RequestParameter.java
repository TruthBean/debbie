/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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

    /**
     * same as name
     * @since 0.0.2
     * @return name
     */
    String value() default "";

    RequestParameterType paramType() default RequestParameterType.MIX;

    String defaultValue() default "";

    boolean require() default true;

    /**
     * if type is BODY, requestType must be specific
     * @return request body media type
     */
    MediaType bodyType() default MediaType.ANY;
}
