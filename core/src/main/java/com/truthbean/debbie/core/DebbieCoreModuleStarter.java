/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.core;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.task.DebbieTaskConfigurer;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;

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
    public void registerBean(ApplicationContext applicationContext, BeanInitialization beanInitialization) {
        var register = new DataTransformerRegister(beanInitialization);
        register.registerTransformer();
    }

    @Override
    public void configure(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        configurationFactory.register(ClassesScanProperties.class, BeanScanConfiguration.class);
        new DebbieTaskConfigurer().configure(applicationContext);
    }

    @Override
    public void starter(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        applicationContext.refreshBeans();
    }

    @Override
    public void release(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        synchronized (DebbieCoreModuleStarter.class) {
            configurationFactory.reset();
            var globalBeanFactory = applicationContext.getGlobalBeanFactory();
            Optional<ThreadPooledExecutor> executor = globalBeanFactory.factoryIfPresent("threadPooledExecutor");
            executor.ifPresent(ThreadPooledExecutor::destroy);
        }
    }
}
