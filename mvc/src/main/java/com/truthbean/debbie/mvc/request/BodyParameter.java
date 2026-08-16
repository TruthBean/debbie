/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
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
 * Binds a method parameter to the HTTP request body.
 *
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

    /** the default value when the body is absent. */
    String defaultValue() default "";

    /** whether the body is required. */
    boolean require() default true;

    /** the expected media type of the body. */
    MediaType type() default MediaType.ANY;
}
