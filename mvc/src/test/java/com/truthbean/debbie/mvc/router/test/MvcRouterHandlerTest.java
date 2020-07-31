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
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;

class MvcRouterHandlerTest {

    private MvcConfiguration mvcConfiguration;

    @BeforeEach
    public void before() {
        ApplicationFactory factory = ApplicationFactory.configure(MvcRouterHandlerTest.class);
        ApplicationContext applicationContext = factory.getApplicationContext();
        BeanInitialization initialization = applicationContext.getBeanInitialization();
        initialization.init(RouterInvokerTest.class);
        applicationContext.refreshBeans();

        DebbieConfigurationCenter configurationFactory = applicationContext.getConfigurationCenter();
        mvcConfiguration = configurationFactory.factory(MvcConfiguration.class, applicationContext);

        MvcRouterRegister.registerRouter(mvcConfiguration, applicationContext);
    }

    @Test
    void getMatchedRouter() {
        var url = "/id/hello";
        // var url = "/id/hello2";
        // var url = "/11-20";
        // var url = "/he/he/11-20";
        DefaultRouterRequest routerRequest = new DefaultRouterRequest();
        routerRequest.setUrl(url);
        routerRequest.setResponseType(MediaType.ANY.info());
        routerRequest.setContentType(MediaType.ANY.info());
        RouterInfo matchedRouter = MvcRouterHandler.getMatchedRouter(routerRequest, mvcConfiguration);
        System.out.println(matchedRouter);
    }

    @Test
    void matchRouterPath() {
        var url = "/id/hello";
        // var url = "/id/hello2";
        // var url = "/11-20";
        // var url = "/he/he/11-20";
        DefaultRouterRequest routerRequest = new DefaultRouterRequest();
        routerRequest.setUrl(url);
        routerRequest.setPathAttributes(new HashMap<>());
        var set = MvcRouterRegister.getRouterInfoSet();
        Set<RouterInfo> routerInfos = MvcRouterHandler.matchRouterPath(url, set, routerRequest);
        System.out.println("----------------------------------------------------------------------");
        for (RouterInfo routerInfo : routerInfos) {
            System.out.println(routerInfo);
            routerInfo.getRequest().getPathAttributes().forEach((key, value) -> {
                System.out.println(key + ":");
                System.out.println(value);
                System.out.println("----------");
            });
        }
    }

    @Test
    void matchRouterPath1() {
    }
}