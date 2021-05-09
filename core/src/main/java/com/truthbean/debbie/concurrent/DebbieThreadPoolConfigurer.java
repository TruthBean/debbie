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

import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public class DebbieThreadPoolConfigurer {
    public void configure(ApplicationContext applicationContext) {
        var register = new SingletonBeanRegister(applicationContext);

        EnvironmentContent envContent = applicationContext.getEnvContent();
        var time = envContent.getLongValue("debbie.thread-pool-executor.await-termination-time", 5000L);
        var factory = new ThreadPooledExecutor(10, 200,
                new NamedThreadFactory().setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler()), time);
        register.registerSingletonBean(factory, ThreadPooledExecutor.class, "threadPooledExecutor");

        applicationContext.refreshBeans();
    }
}
