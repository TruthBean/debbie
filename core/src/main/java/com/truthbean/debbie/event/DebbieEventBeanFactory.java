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

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.GlobalBeanFactory;

/**
 * @author TruthBean
 * @since 0.1.0
 * Created on 2020/7/13 16:12.
 */
public class DebbieEventBeanFactory<Bean extends AbstractDebbieEvent> implements BeanFactory<Bean> {

    private final DebbieBeanInfo<Bean> beanInfo;
    private GlobalBeanFactory globalBeanFactory;

    public DebbieEventBeanFactory(DebbieBeanInfo<Bean> beanInfo) {
        this.beanInfo = beanInfo;
    }

    @Override
    public Bean getBean() {
        return globalBeanFactory.factoryBeanByDependenceProcessor(beanInfo, true, this);
    }

    @Override
    public Class<Bean> getBeanType() {
        return beanInfo.getBeanClass();
    }

    @Override
    public boolean isSingleton() {
        return beanInfo.isSingleton();
    }

    @Override
    public void destroy() {
        beanInfo.setBean(() -> null);
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }
}
