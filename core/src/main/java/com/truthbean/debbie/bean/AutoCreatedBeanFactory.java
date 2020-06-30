/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;

import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-01 14:56
 */
public class AutoCreatedBeanFactory {

    private final BeanFactoryContext applicationContext;
    private final BeanInitialization beanInitialization;
    public AutoCreatedBeanFactory(BeanFactoryContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanInitialization = applicationContext.getBeanInitialization();
    }

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("AutoCreatedBeanFactory", true);
    private final ThreadPooledExecutor autoCreatedBeanExecutor = new ThreadPooledExecutor(1, 1, namedThreadFactory);

    public void autoCreateBeans() {
        final Set<DebbieBeanInfo<?>> autoCreatedBean = applicationContext.getAutoCreatedBean();
        autoCreatedBeanExecutor.execute(() -> {
            for (DebbieBeanInfo<?> beanInfo : autoCreatedBean) {
                Boolean lazyCreate = beanInfo.getLazyCreate();
                if (lazyCreate != null && !lazyCreate) {
                    beanInfo.setBean(applicationContext.factory(beanInfo.getServiceName()));
                    beanInitialization.refreshBean(beanInfo);
                }
            }
        });
    }

    public void stopAll() {
        autoCreatedBeanExecutor.destroy();
    }
}
