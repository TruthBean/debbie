/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router.test;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class MvcRouterRegisterTest {

    @Test
    public void testRegisterRouter() {
        ApplicationFactory factory = ApplicationFactory.configure(MvcRouterRegisterTest.class);
        var context = factory.getApplicationContext();
        BeanInitialization initialization = context.getBeanInitialization();
        initialization.init(RouterInvokerTest.class);
        context.refreshBeans();

        DebbieConfigurationCenter configurationFactory = context.getConfigurationCenter();
        MvcConfiguration mvcConfiguration = configurationFactory.factory(MvcConfiguration.class, context);

        MvcRouterRegister.registerRouter(mvcConfiguration, context);

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Set<RouterInfo> routerInfoSet = MvcRouterRegister.getRouterInfoSet();
        for (RouterInfo routerInfo : routerInfoSet) {
            System.out.println(routerInfo.getPaths());
        }
    }
}
