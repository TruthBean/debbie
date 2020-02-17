package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MvcRouterHandlerTest {

    private MvcConfiguration mvcConfiguration;

    @BeforeEach
    public void before() {
        DebbieApplicationFactory factory = new DebbieApplicationFactory(MvcRouterHandlerTest.class);
        factory.config();
        factory.callStarter();
        BeanInitialization initialization = factory.getBeanInitialization();
        initialization.init(RouterInvokerTest.class);
        factory.refreshBeans();

        DebbieConfigurationFactory configurationFactory = factory.getConfigurationFactory();
        mvcConfiguration = configurationFactory.factory(MvcConfiguration.class, factory);

        MvcRouterRegister.registerRouter(mvcConfiguration, factory);
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