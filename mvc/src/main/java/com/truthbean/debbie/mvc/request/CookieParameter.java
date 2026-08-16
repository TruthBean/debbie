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

import java.lang.annotation.*;

/**
 * Binds a method parameter to an HTTP cookie value.
 *
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieParameter {

    /** the cookie name; if blank, the parameter name is used. */
    String name() default "";

    /**
     * same as name
     * @return name
     */
    String value() default "";

    /** the default value when the cookie is absent. */
    String defaultValue() default "";

    /** whether the cookie is required. */
    boolean require() default true;
}
