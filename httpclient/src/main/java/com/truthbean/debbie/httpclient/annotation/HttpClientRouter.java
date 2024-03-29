/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.annotation;

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.bean.*;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-04-03 17:12.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent(type = BeanType.SINGLETON)
public @interface HttpClientRouter {
    /**
     * bean name
     * @return bean name
     */
    @AliasFor(attribute = "name", annotation = BeanComponent.class)
    String value() default "";

    String[] baseUrl() default {""};

    /**
     * @return the failure action type, that must be the client implement class
     */
    Class<?> failureAction() default Void.class;

    /**
     * @return @see BeanComponent#conditions
     */
    @AliasFor(attribute = "conditions", annotation = BeanComponent.class)
    Class<? extends BeanCondition>[] conditions() default {};
}
