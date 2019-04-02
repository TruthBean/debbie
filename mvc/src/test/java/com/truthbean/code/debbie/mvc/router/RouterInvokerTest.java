package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.aop.InterfaceDynamicProxy;
import com.truthbean.code.debbie.core.bean.BeanFactory;
import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import net.sf.cglib.proxy.InterfaceMaker;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

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
    public void testProxy() {
        var proxy = new InterfaceDynamicProxy();
        System.out.println(proxy);
        var test = proxy.doProxy(RouterInvokerTest.class);
        System.out.println(test.router("6666666666666"));
    }

    @Router
    public String router(String a) {
        return a;
    }

}