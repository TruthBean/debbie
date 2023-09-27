/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-01 14:56
 */
class AutoCreatedBeanFactory {

    private final ThreadPooledExecutor autoCreatedBeanExecutor;
    private volatile Future<?> autoCreatedBeanFactoryThreadFuture;

    AutoCreatedBeanFactory(DebbieApplication application) {
        final ThreadFactory namedThreadFactory = new NamedThreadFactory("AutoCreatedBeanFactory", true)
                .setUncaughtExceptionHandler((t, e) -> {
                    LOGGER.error("Bean created error(" + e.getMessage() + ") in thread(" + t.getId() + ", " + t.getName() + "), application will shutdown. ", e);
                    application.exit();
                    t.interrupt();
                });
        autoCreatedBeanExecutor = new ThreadPooledExecutor(1, 1, 1, namedThreadFactory, 5000L);
    }

    void autoCreateBeans(DebbieApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        autoCreatedBeanFactoryThreadFuture = autoCreatedBeanExecutor.submit(() -> {
            if (!applicationContext.isExiting()) {
                beanInfoManager.autoCreateSingletonBeans(applicationContext);
            }
        });
    }

    void stopAll() {
        LOGGER.debug("AutoCreatedBeanFactory thread will interrupt...");
        if (autoCreatedBeanFactoryThreadFuture != null && !autoCreatedBeanFactoryThreadFuture.isDone() && !autoCreatedBeanFactoryThreadFuture.isCancelled()) {
            autoCreatedBeanFactoryThreadFuture.cancel(true);
        }
        autoCreatedBeanExecutor.destroy();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCreatedBeanFactory.class);
}
