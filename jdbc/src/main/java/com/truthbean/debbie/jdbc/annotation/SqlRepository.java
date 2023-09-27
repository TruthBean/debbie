/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanCondition;
import com.truthbean.debbie.bean.BeanType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent(type = BeanType.SINGLETON)
public @interface SqlRepository {

    @AliasFor(attribute = "value", annotation = BeanComponent.class)
    String value() default "";

    /**
     * @return @see BeanComponent#conditions
     */
    @AliasFor(attribute = "conditions", annotation = BeanComponent.class)
    Class<? extends BeanCondition>[] conditions() default {};
}
