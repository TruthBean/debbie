/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-07 15:31
 */
public interface MutableBeanInfo<Bean> extends BeanInfo<Bean> {

    void setBeanFactory(BeanFactory<Bean> beanFactory);

    void setBeanProxyType(BeanProxyType beanProxyType);

    void setBeanType(BeanType beanType);

    void addBeanName(String beanName);

    void addBeanNames(Set<String> beanNames);

    void setBean(Bean bean);

    void setBean(Supplier<Bean> bean);

    void addProperty(String name, Object value);

    Object getProperty(String name);

    @Override
    default void release() {
        BeanFactory<Bean> beanFactory = getBeanFactory();
        if (beanFactory != null) {
            beanFactory.destroy();
            setBeanFactory(null);
        } else {
            close();
        }
        setBean((Bean) null);
    }
}
