/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.concurrent;

import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.core.concurrent.ThreadLoggerUncaughtExceptionHandler;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public class DebbieThreadPoolConfigurer {

    private final String threadPoolExecutorAwaitTerminationTimeKey = "debbie.thread-pool-executor.await-termination-time";

    public ThreadPooledExecutor configure(ApplicationContext applicationContext) {

        EnvironmentDepositoryHolder environmentDepositoryHolder = applicationContext.getEnvironmentHolder();
        Environment environment = environmentDepositoryHolder.getEnvironmentIfPresent(environmentDepositoryHolder.getDefaultProfile());
        var time = environment.getLongValue(threadPoolExecutorAwaitTerminationTimeKey, 5000L);
        var core = Runtime.getRuntime().availableProcessors();
        var max = core * 100;
        var queueLength = max * 100;
        var factory = new ThreadPooledExecutor(core, max, queueLength,
                new NamedThreadFactory().setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler()), time);

        var simpleBeanFactory = new SimpleBeanFactory<>(factory, ThreadPooledExecutor.class, BeanProxyType.JDK, "threadPooledExecutor");
        applicationContext.getBeanInfoManager().registerBeanInfo(simpleBeanFactory);
        return factory;
    }
}
