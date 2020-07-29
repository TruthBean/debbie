/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieTaskConfigurer {

    public void configure(ApplicationContext applicationContext) {
        TaskFactory debbieTaskFactory = new TaskFactory();
        SingletonBeanRegister register = new SingletonBeanRegister(applicationContext);

        debbieTaskFactory.setApplicationContext(applicationContext);

        ThreadPooledExecutor factory = new ThreadPooledExecutor();
        register.registerSingletonBean(factory, ThreadPooledExecutor.class, "threadPooledExecutor");

        debbieTaskFactory.registerTask();
        register.registerSingletonBean(debbieTaskFactory, TaskFactory.class, "taskFactory");
        applicationContext.refreshBeans();
    }
}
