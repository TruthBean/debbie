/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanComponent {

    /**
     * same as VALUE
     * @since 0.0.2
     * @return bean name
     */
    @AliasFor(attribute = "VALUE")
    String name() default "";

    /**
     * @since 0.0.1
     * @return bean name
     */
    @AliasFor(attribute = "name")
    String value() default "";

    BeanType type() default BeanType.NO_LIMIT;

    BeanProxyType proxy() default BeanProxyType.JDK;

    /**
     * lazy create bean, default true
     * @return boolean
     */
    boolean lazy() default true;

    /**
     * bean factory of the bean
     * @return BeanFactory class
     */
    Class<? extends BeanFactory> factory() default BeanFactory.class;

    /**
     * All {@link BeanCondition} classes that must {@linkplain BeanCondition#matches match}
     * in order for the component to be registered.
     */
    Class<? extends BeanCondition>[] conditions() default DefaultBeanCondition.class;

}
