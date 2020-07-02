/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RmiModuleStarter implements DebbieModuleStarter {

    @Override
    public void registerBean(DebbieApplicationContext applicationContext, BeanInitialization beanInitialization) {
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, DebbieApplicationContext applicationContext) {
        configurationFactory.register(RmiServerProperties.class, RmiServerConfiguration.class);

        RmiServerConfiguration configuration = configurationFactory.factory(RmiServerConfiguration.class, applicationContext);

        // 注册管理器
        var register = new RemoteServiceRegister(applicationContext, configuration.getRmiBindAddress(), configuration.getRmiBindPort());
        // bind
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();
        Set<Class<?>> rmiServiceMappers = getRmiServiceMappers(beanInitialization);
        for (Class<?> rmiServiceMapper : rmiServiceMappers) {
            register.bind(rmiServiceMapper);
        }
    }

    private Set<Class<?>> getRmiServiceMappers(BeanInitialization beanInitialization) {
        Set<Class<?>> rmiServiceMappers = new LinkedHashSet<>();
        Set<DebbieBeanInfo<?>> beanInfos = beanInitialization.getRegisteredRawBeans();
        for (DebbieBeanInfo<?> beanInfo : beanInfos) {
            DebbieRmiMapper classAnnotation = beanInfo.getClassAnnotation(DebbieRmiMapper.class);
            if (classAnnotation != null && beanInfo.getBeanClass().isInterface()) {
                rmiServiceMappers.add(beanInfo.getBeanClass());
            }
        }
        return rmiServiceMappers;
    }

    @Override
    public int getOrder() {
        return 12;
    }

    @Override
    public void release(DebbieConfigurationFactory configurationFactory, DebbieApplicationContext applicationContext) {
        // todo
    }
}
