/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.core;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.concurrent.DebbieThreadPoolConfigurer;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.task.DebbieTaskConfigurer;
import com.truthbean.debbie.task.TaskFactory;

import java.util.Optional;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieCoreModuleStarter implements DebbieModuleStarter {

    public DebbieCoreModuleStarter() {
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var register = new DataTransformerRegister(beanInfoManager);
        register.registerTransformer();

        boolean containsBean = beanInfoManager.containsBean(BeanScanConfiguration.class);
        if (!containsBean) {
            var beanFactory = new PropertiesConfigurationBeanFactory<>(new ClassesScanProperties(), BeanScanConfiguration.class);
            beanInfoManager.register(beanFactory);
        }
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
        DebbieThreadPoolConfigurer configurer = new DebbieThreadPoolConfigurer();
        configurer.configure(applicationContext);

        DebbieTaskConfigurer debbieTaskConfigurer = new DebbieTaskConfigurer();
        debbieTaskConfigurer.configure(applicationContext);
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        TaskFactory taskFactory = applicationContext.getGlobalBeanFactory().factory("taskFactory", TaskFactory.class, false);
        if (taskFactory != null) {
            taskFactory.registerTask();
        }
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        EnvironmentContent envContent = applicationContext.getEnvContent();
        boolean value = envContent.getBooleanValue("debbie.task.enable", true);
        if (value) {
            GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
            // do task
            Optional<TaskFactory> taskFactory = globalBeanFactory.factoryIfPresent("taskFactory");
            taskFactory.ifPresent((it) -> {
                it.prepare();
                it.doTask();
            });
        }
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        synchronized (DebbieCoreModuleStarter.class) {
            var globalBeanFactory = applicationContext.getGlobalBeanFactory();
            Optional<ThreadPooledExecutor> executor = globalBeanFactory.factoryIfPresent("threadPooledExecutor");
            executor.ifPresent(ThreadPooledExecutor::destroy);
            Optional<TaskFactory> taskFactory = globalBeanFactory.factoryIfPresent("taskFactory");
            taskFactory.ifPresent(factory -> factory.destruct(applicationContext));
        }
    }
}
