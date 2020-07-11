/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.jdbc.column.PrimaryKeyType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 15:47.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlColumn {
    /**
     * is id
     * @return whether is id
     */
    boolean id() default false;
    PrimaryKeyType primaryKey() default PrimaryKeyType.NONE;

    /**
     * @return column name
     */
    String value() default "";

    /**
     * @return column name
     */
    String name() default "";

    boolean nullable() default true;

    boolean unique() default false;

    int charMaxLength() default 64;

    String defaultValue() default "";

    String comment() default "";

    /**
     * TODO
     * @return column definition
     */
    String columnDefinition() default "";
}
