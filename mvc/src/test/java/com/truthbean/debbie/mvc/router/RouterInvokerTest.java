package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.proxy.JdkDynamicProxy;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:56.
 */
public class RouterInvokerTest {

    @Test
    public void testRouterInvoke() {
        DebbieApplicationFactory factory = new DebbieApplicationFactory(RouterInvokerTest.class);
        factory.config();
        factory.callStarter();
        BeanInitialization initialization = factory.getBeanInitialization();
        initialization.init(RouterInvokerTest.class);
        factory.refreshBeans();
        var router = factory.factoryBeanInvoker(RouterInvokerTest.class);
        var params = new Object[]{"哈哈"};
        var result = router.invokeMethod(Router.class, "router", params);
        System.out.println(result);
    }

    @Test
    public void testJdkProxy() {
        var that = new RouterInvokerTest();
        var proxy = new JdkDynamicProxy();
        /*RouterInvokerTestInterface test = proxy.doJdkProxy(RouterInvokerTestInterface.class, that);
        System.out.println(test.router("6666666666666"));*/
    }

    @Router
    public String router(String a) {
        return a;
    }

}