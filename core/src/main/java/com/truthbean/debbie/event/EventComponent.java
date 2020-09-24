/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.bean.*;

import java.lang.annotation.*;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent(type = BeanType.NO_LIMIT, factory = DebbieEventBeanFactory.class)
public @interface EventComponent {

    /**
     * event bean name
     * @return router bean name
     */
    @AliasFor(attribute = "value", annotation = BeanComponent.class)
    String value() default "";

    /**
     * event bean name
     * @return router bean name
     */
    @AliasFor(attribute = "name", annotation = BeanComponent.class)
    String name() default "";
}
