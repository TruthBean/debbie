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
import com.truthbean.debbie.data.transformer.DataTransformerRegister;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.task.DebbieTaskConfigurer;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieCoreModuleStarter implements DebbieModuleStarter {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void registerBean(DebbieApplicationContext applicationContext, BeanInitialization beanInitialization) {
        DataTransformerRegister register = new DataTransformerRegister(beanInitialization);
        register.registerTransformer();
    }

    @Override
    public void configure(DebbieConfigurationCenter configurationFactory, DebbieApplicationContext applicationContext) {
        configurationFactory.register(ClassesScanProperties.class, BeanScanConfiguration.class);
        new DebbieTaskConfigurer().configure(applicationContext);
    }

    @Override
    public void starter(DebbieConfigurationCenter configurationFactory, DebbieApplicationContext applicationContext) {
        applicationContext.refreshBeans();
    }

    @Override
    public void release(DebbieConfigurationCenter configurationFactory, DebbieApplicationContext applicationContext) {
        configurationFactory.reset();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        ThreadPooledExecutor executor = globalBeanFactory.factory("threadPooledExecutor");
        executor.destroy();
    }
}
