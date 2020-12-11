/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-24 15:41
 */
public class BeanComponentInfo {
    private String name;

    private BeanType type;

    private boolean lazy;

    private Class<? extends BeanFactory<?>> factory;

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
        if (factory != null)
            this.factory = (Class<? extends BeanFactory<?>>) factory;
    }
}
