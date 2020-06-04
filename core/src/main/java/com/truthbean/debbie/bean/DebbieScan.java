/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.properties.ClassesScanProperties;

import java.lang.annotation.*;
import java.util.Set;

/**
 * @see ClassesScanProperties
 *
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-19 10:49.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieScan {

    String[] basePackages() default {};

    Class<?>[] classes() default {};

    String[] excludePackages() default {};

    Class<?>[] excludeClasses() default {};
}
