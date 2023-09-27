/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieTaskConfigurer {

    private static final String ENABLE_KEY = "debbie.task.enable";

    public boolean enable(Environment environment) {
        return environment.getBooleanValue(ENABLE_KEY, true);
    }

    public void configure(ApplicationContext applicationContext) {
        EnvironmentDepositoryHolder environmentDepositoryHolder = applicationContext.getEnvironmentHolder();
        Environment environment = environmentDepositoryHolder.getEnvironmentIfPresent(environmentDepositoryHolder.getDefaultProfile());
        if (!enable(environment)) {
            LOGGER.info("debbie task is not enable");
            return;
        }

        var debbieTaskFactory = new TaskFactory();
        // var register = new SingletonBeanRegister(applicationContext);

        debbieTaskFactory.setApplicationContext(applicationContext);
        BeanInfoManager infoManager = applicationContext.getBeanInfoManager();

        // debbieTaskFactory.registerTask();
        // register.registerSingletonBean(debbieTaskFactory, TaskFactory.class, "taskRegister", "taskFactory");

        var beanFactory = new SimpleBeanFactory<>(debbieTaskFactory, TaskFactory.class, BeanProxyType.JDK, "taskRegister", "taskFactory");
        infoManager.registerBeanInfo(beanFactory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieTaskConfigurer.class);
}
