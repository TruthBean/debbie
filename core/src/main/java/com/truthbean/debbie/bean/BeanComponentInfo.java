/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * <a href="http://license.coscl.org.cn/MulanPSL2">http://license.coscl.org.cn/MulanPSL2</a>
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-24 15:41
 */
public class BeanComponentInfo {
    private String name;

    private BeanType type;

    private BeanProxyType proxy = BeanProxyType.JDK;

    private boolean lazy;

    private Class<? extends BeanFactory<?>> factory;

    private final Set<Class<? extends BeanCondition>> conditions = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasName() {
        return this.name != null && !this.name.isBlank();
    }

    public BeanType getType() {
        return type;
    }

    public void setType(BeanType type) {
        this.type = type;
    }

    public BeanProxyType getProxy() {
        return proxy;
    }

    public void setProxy(BeanProxyType proxy) {
        if (proxy != null) {
            this.proxy = proxy;
        }
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        if (lazy == null) {
            // default value is true
            this.lazy = true;
            return;
        }

        this.lazy = lazy;
    }

    public Class<? extends BeanFactory<?>> getFactory() {
        return factory;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setFactory(Class<? extends BeanFactory> factory) {
        if (factory != null) {
            this.factory = (Class<? extends BeanFactory<?>>) factory;
        }
    }

    public Set<Class<? extends BeanCondition>> getCondition() {
        return conditions;
    }

    public void setCondition(Class<? extends BeanCondition>[] condition) {
        if (condition != null && condition.length > 0) {
            Collections.addAll(this.conditions, condition);
        }
    }
}
