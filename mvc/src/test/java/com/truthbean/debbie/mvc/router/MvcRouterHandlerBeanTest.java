/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Set;

@ExtendWith({DebbieApplicationExtension.class})
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
            System.out.println(routerInfo.getMethod());
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