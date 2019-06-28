package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class MvcRouterRegisterTest {

    @Test
    public void testRegisterRouter() {
        DebbieApplicationFactory factory = new DebbieApplicationFactory();
        factory.config();
        factory.callStarter();
        BeanInitialization initialization = factory.getBeanInitialization();
        initialization.init(RouterInvokerTest.class);
        factory.refreshBeans();

        DebbieConfigurationFactory configurationFactory = factory.getConfigurationFactory();
        MvcConfiguration mvcConfiguration = configurationFactory.factory(MvcConfiguration.class, factory);

        MvcRouterRegister.registerRouter(mvcConfiguration, factory);

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Set<RouterInfo> routerInfoSet = MvcRouterRegister.getRouterInfoSet();
        for (RouterInfo routerInfo : routerInfoSet) {
            System.out.println(routerInfo.getPaths());
        }
    }
}
