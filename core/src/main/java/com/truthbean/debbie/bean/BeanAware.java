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

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-14 14:44
 */
public interface BeanAware<Bean> extends Aware {

    Class<Bean> getBeanClass();

    default <T extends Bean> void setBean(T bean) {
    }

    default <T extends Bean> void setBean(Supplier<BaseBeanCreator<T>> supplier) {
        setBean(supplier.get().create());
    }

    default void setBeans(Collection<Bean> beans) {
    }
}
