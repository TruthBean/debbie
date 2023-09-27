/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.domain;

import java.lang.annotation.*;


/**
 * Annotation to define the default {@link Sort} options to be used when injecting a {@link Sort} instance into a
 * controller handler method.
 *
 * @author Oliver Gierke
 * @since 1.6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SortDefault {

    /**
     * @return Alias for {@link #sort()} to make a declaration configuring fields only more concise.
     */
    String[] value() default {};

    /**
     * @return The properties to sort by by default. If unset, no sorting will be applied at all.
     */
    String[] sort() default {};

    /**
     * @return The direction to sort by. Defaults to {@link Sort.Direction#ASC}.
     */
    Sort.Direction direction() default Sort.Direction.ASC;

    /**
     * Wrapper annotation to allow declaring multiple {@link SortDefault} annotations on a method parameter.
     *
     * @author Oliver Gierke
     * @since 1.6
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface SortDefaults {

        /**
         * @return The individual {@link SortDefault} declarations to be sorted by.
         */
        SortDefault[] value();
    }
}