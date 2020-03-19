package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.BeforeEach;
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