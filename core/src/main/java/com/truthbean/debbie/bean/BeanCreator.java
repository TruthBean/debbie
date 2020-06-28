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

import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-24 09:59.
 */
public interface BeanCreator<Bean> extends BaseBeanCreator<Bean>,
        InjectedBeanFactoryAware, BeanDependenceProcessorAware {

    void createPreparation(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap);

    void postConstructor();

    void postPreparation();

    void postCreated();

    boolean isCreated();

    Bean getCreatedBean();
}
