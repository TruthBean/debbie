package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.bean.BeanFactory;
import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.core.proxy.InterfaceDynamicProxy;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:56.
 */
public class RouterInvokerTest {

    @Test
    public void testRouterInvoke() {
        BeanInitializationHandler.init(RouterInvokerTest.class);
        var router = BeanFactory.factory(RouterInvokerTest.class);
        var params = new Object[]{"哈哈"};
        var result = router.invokeMethod(Router.class, "router", params);
        System.out.println(result);
    }

    @Test
    public void testCglibProxy() {
        var proxy = new InterfaceDynamicProxy();
        RouterInvokerTest test = (RouterInvokerTest) proxy.doCglibProxy(RouterInvokerTest.class);
        System.out.println(test.router("6666666666666"));
    }

    @Test
    public void testJdkProxy() {
        var that = new RouterInvokerTest();
        var proxy = new InterfaceDynamicProxy();
        /*RouterInvokerTestInterface test = proxy.doJdkProxy(RouterInvokerTestInterface.class, that);
        System.out.println(test.router("6666666666666"));*/
    }

    @Router
    public String router(String a) {
        return a;
    }

}