/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router.test;

import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;

@DebbieApplicationTest
class MvcRouterHandlerBeanTest {

    @Test
    void test() {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Set<RouterInfo> routerInfoSet = MvcRouterRegister.getRouterInfoSet();
        for (RouterInfo routerInfo : routerInfoSet) {
            System.out.println(routerInfo.getPaths());
        }
    }

    @Test
    void matchRouterPath() {
        // var url = "/id/hello";
        var url = "/id/hello2.do";
        // var url = "/11-20";
        // var url = "/he/he/11o-20";
        // 00var url = "/hehehehehehe";
        DefaultRouterRequest routerRequest = new DefaultRouterRequest();
        routerRequest.setUrl(url);
        routerRequest.setPathAttributes(new HashMap<>());
        var set = MvcRouterRegister.getRouterInfoSet();
        Set<RouterInfo> routerInfos = MvcRouterHandler.matchRouterPath(url, set, routerRequest);
        System.out.println("----------------------------------------------------------------------");
        for (RouterInfo routerInfo : routerInfos) {
            System.out.println();
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