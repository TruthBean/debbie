/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * <a href="http://license.coscl.org.cn/MulanPSL2">http://license.coscl.org.cn/MulanPSL2</a>
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.survey.bean;

import com.truthbean.debbie.bean.BeanCondition;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-04-14 21:18
 */
public interface BeanDefinition extends BeanInfo {

    void setProfile(String profile);

    void setName(String name);

    void setScope(String scope);

    void setBeanProxyType(BeanProxyType beanProxyType);

    <T> void setBeanClass(Class<T> beanClass);

    void setConditions(Set<BeanCondition> conditions);

    void setLazyCreate(boolean lazyCreate);

    <T> void setValue(T bean);
}
