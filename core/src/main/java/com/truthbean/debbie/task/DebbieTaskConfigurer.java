/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieTaskConfigurer {

    private static final String ENABLE_KEY = "debbie.task.enable";

    public boolean enable(EnvironmentContent envContent) {
        return envContent.getBooleanValue(ENABLE_KEY, true);
    }

    public void configure(ApplicationContext applicationContext) {
        EnvironmentContent envContent = applicationContext.getEnvContent();
        if (!enable(envContent)) {
            LOGGER.info("debbie task is not enable");
            return;
        }

        var debbieTaskFactory = new TaskFactory();
        var register = new SingletonBeanRegister(applicationContext);

        debbieTaskFactory.setApplicationContext(applicationContext);


        debbieTaskFactory.registerTask();
        register.registerSingletonBean(debbieTaskFactory, TaskFactory.class, "taskFactory");
        register.registerSingletonBean(debbieTaskFactory, TaskRegister.class, "taskRegister");
        applicationContext.refreshBeans();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieTaskConfigurer.class);
}
