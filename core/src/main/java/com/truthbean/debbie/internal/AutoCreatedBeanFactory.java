/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.logger.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-01 14:56
 */
class AutoCreatedBeanFactory {

    private final GlobalBeanFactory globalBeanFactory;
    private final DebbieBeanInfoFactory beanInfoFactory;
    private final BeanInitialization beanInitialization;
    AutoCreatedBeanFactory(DebbieApplicationContext applicationContext) {
        this.beanInfoFactory = applicationContext.getBeanInfoFactory();
        this.beanInitialization = applicationContext.getBeanInitialization();
        this.globalBeanFactory = applicationContext.getGlobalBeanFactory();
    }

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("AutoCreatedBeanFactory", true);
    private final ThreadPooledExecutor autoCreatedBeanExecutor = new ThreadPooledExecutor(1, 1, namedThreadFactory);

    @SuppressWarnings({"unchecked", "rawtypes"})
    void autoCreateBeans() {
        final Set<BeanInfo<?>> autoCreatedBean = beanInfoFactory.getAutoCreatedBean();
        autoCreatedBeanExecutor.execute(() -> {
            for (BeanInfo localBeanInfo : autoCreatedBean) {
                boolean lazyCreate = localBeanInfo.isLazyCreate();
                if (!lazyCreate && localBeanInfo instanceof MutableBeanInfo) {
                    try {
                        MutableBeanInfo mutableBeanInfo = (MutableBeanInfo) localBeanInfo;
                        mutableBeanInfo.setBean(globalBeanFactory.factory(localBeanInfo));
                        beanInitialization.refreshBean(mutableBeanInfo);
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
            }
        });
    }

    void stopAll() {
        autoCreatedBeanExecutor.destroy();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCreatedBeanFactory.class);
}
