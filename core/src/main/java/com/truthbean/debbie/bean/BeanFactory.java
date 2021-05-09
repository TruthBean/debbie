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

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 16:50.
 */
public interface BeanFactory<Bean> extends GlobalBeanFactoryAware, BeanClosure {

    Bean getBean();

    default Bean factoryBean() {
        return getBean();
    }

    /**
     * 不用Class&lt;Bean&gt;或者Class&lt;? extends Bean&gt;的原因是，考虑到泛型问题
     * @return Class&lt;? extends Bean&gt;
     */
    Class<?> getBeanType();

    boolean isSingleton();

    default boolean isSkipCreatedBeanFactory() {
        return SkipCreatedBeanFactory.class.isAssignableFrom(this.getClass());
    }
}
