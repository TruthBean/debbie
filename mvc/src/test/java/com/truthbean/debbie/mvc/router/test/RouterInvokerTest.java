/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router.test;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:56.
 */
public class RouterInvokerTest {

    @Test
    public void testRouterInvoke() {
        ApplicationFactory factory = ApplicationFactory.configure(RouterInvokerTest.class);
        var context = factory.getApplicationContext();
        BeanInfoManager infoManager = context.getBeanInfoManager();
        BeanInfoManager beanInfoFactory = context.getBeanInfoManager();
        infoManager.register(RouterInvokerTest.class);
        // var router = factory.factoryBeanInvoker(RouterInvokerTest.class);
        var params = new Object[]{"哈哈"};
        // var result = router.invokeMethod(Router.class, "router", params);
        // System.out.println(result);
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