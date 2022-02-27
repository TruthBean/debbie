/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.logger.LogLevel;

import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-01 14:56
 */
class AutoCreatedBeanFactory {

    private final ThreadPooledExecutor autoCreatedBeanExecutor;

    AutoCreatedBeanFactory(DebbieApplication application) {
        final ThreadFactory namedThreadFactory = new NamedThreadFactory("AutoCreatedBeanFactory", true)
                .setUncaughtExceptionHandler((t, e) -> {
                    LOGGER.error("Bean created error(" + e.getMessage() + ") in thread(" + t.getId() + ", " + t.getName() + "), application will shutdown. ", e);
                    application.exit();
                });
        autoCreatedBeanExecutor = new ThreadPooledExecutor(1, 1, namedThreadFactory);
    }

    void autoCreateBeans(DebbieApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        autoCreatedBeanExecutor.execute(() -> {
            if (!applicationContext.isExiting()) {
                beanInfoManager.autoCreateSingletonBeans(applicationContext);
            }
        });
    }

    void stopAll() {
        autoCreatedBeanExecutor.destroy();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCreatedBeanFactory.class);
}
