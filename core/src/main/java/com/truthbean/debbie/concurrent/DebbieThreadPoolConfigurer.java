/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.concurrent;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public class DebbieThreadPoolConfigurer {

    private final String threadPoolExecutorAwaitTerminationTimeKey = "debbie.thread-pool-executor.await-termination-time";

    public ThreadPooledExecutor configure(ApplicationContext applicationContext) {

        EnvironmentContent envContent = applicationContext.getEnvContent();
        var time = envContent.getLongValue(threadPoolExecutorAwaitTerminationTimeKey, 5000L);
        var core = Runtime.getRuntime().availableProcessors();
        var max = core * 100;
        var queueLength = max * 100;
        var factory = new ThreadPooledExecutor(core, max, queueLength,
                new NamedThreadFactory().setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler()), time);
        applicationContext.registerSingleBean(ThreadPooledExecutor.class, factory, "threadPooledExecutor");
        return factory;
    }
}
