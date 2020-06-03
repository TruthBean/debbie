/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:36.
 */
public class JdkDynamicProxy<T, K extends T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdkDynamicProxy.class);

    public T invokeJdkProxy(BeanFactoryHandler handler, Class<T> targetInterface, K target) {
        InvocationHandler invocationHandler = new ProxyInvocationHandler<>(target, handler);

        return doJdkProxy(targetInterface, target, invocationHandler);
    }

    public T doJdkProxy(Class<T> targetInterface, K target, InvocationHandler invocationHandler) {
        var targetClass = target.getClass();
        var classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        var interfaces = new Class[]{targetInterface};

        var proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        var proxyClass = proxyInstance.getClass();

        LOGGER.trace("proxy(" + proxyClass.getName() + ") interface(" + targetInterface.getName() + ") with class(" + targetClass.getName() + ")");

        T result;

        try {
            result = targetInterface.cast(proxyInstance);
        } catch (Exception e) {
            LOGGER.warn(proxyClass.getName() + " cast to " + targetClass.getName() + " error", e);
            // handle error
            result = target;
        }

        // do after
        LOGGER.trace("after proxy ....");

        return result;
    }

    public static <K> K getRealValue(Object proxyValue) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxyValue);
        if (invocationHandler instanceof ProxyInvocationHandler) {
            @SuppressWarnings("unchecked")
            ProxyInvocationHandler<K> proxyInvocationHandler = (ProxyInvocationHandler) invocationHandler;
            return proxyInvocationHandler.getRealTarget();
        }
        return null;
    }

}
