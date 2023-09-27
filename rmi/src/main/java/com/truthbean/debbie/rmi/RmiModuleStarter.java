/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RmiModuleStarter implements DebbieModuleStarter {

    @Override
    public boolean enable(Environment environment) {
        return environment.getBooleanValue(RmiServerProperties.ENABLE_KEY, false);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var beanFactory = new PropertiesConfigurationBeanFactory<>(new RmiServerProperties(), RmiServerConfiguration.class);
        beanInfoManager.registerBeanInfo(beanFactory);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
        RmiServerConfiguration configuration = applicationContext.factory(RmiServerConfiguration.class);

        // 注册管理器
        var register = new RemoteServiceRegister(applicationContext, configuration.getRmiBindAddress(), configuration.getRmiBindPort());
        // bind
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        Set<Class<?>> rmiServiceMappers = getRmiServiceMappers(beanInfoManager);
        for (Class<?> rmiServiceMapper : rmiServiceMappers) {
            register.bind(rmiServiceMapper);
        }
    }

    private Set<Class<?>> getRmiServiceMappers(BeanInfoManager beanInfoManager) {
        Set<Class<?>> rmiServiceMappers = new LinkedHashSet<>();
        /*Set<BeanInfo<?>> beanInfoSet = beanInfoManager.getRegisteredRawBeans();
        for (BeanInfo<?> beanInfo : beanInfoSet) {
            if (beanInfo instanceof ClassBeanInfo) {
                ClassBeanInfo<?> classBeanInfo = (ClassBeanInfo<?>) beanInfo;
                DebbieRmiMapper classAnnotation = classBeanInfo.getClassAnnotation(DebbieRmiMapper.class);
                if (classAnnotation != null && classBeanInfo.getBeanClass().isInterface()) {
                    rmiServiceMappers.add(classBeanInfo.getBeanClass());
                }
            }
        }*/
        return rmiServiceMappers;
    }

    @Override
    public int getOrder() {
        return 12;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        // todo
    }
}
