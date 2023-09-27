/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.mvc.router.RouterBeanLifecycle;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MvcModuleStarter implements DebbieModuleStarter {
    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
        MvcProperties mvcProperties = new MvcProperties();
        MvcConfiguration mvcConfiguration = mvcProperties.getConfiguration(applicationContext);

        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        var beanFactory = new PropertiesConfigurationBeanFactory<>(mvcProperties, MvcConfiguration.class);
        beanInfoManager.registerBeanInfo(beanFactory);

        applicationContext.registerBeanLifecycle(new RouterBeanLifecycle(MvcRouterRegister.getInstance(mvcConfiguration)));
    }

    @Override
    public int getOrder() {
        return 11;
    }
}
