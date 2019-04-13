package com.truthbean.code.debbie.core.proxy;

import com.truthbean.code.debbie.core.reflection.ClassLoaderUtils;
import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import net.sf.cglib.proxy.InterfaceMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:36.
 */
public class InterfaceDynamicProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDynamicProxy.class);

    public <T> T doCglibProxy(Class<T> targetClass) {
        try {
            T target = ReflectionHelper.newInstance(targetClass);
            InterfaceMaker interfaceMaker = new InterfaceMaker();
            //抽取某个类的方法生成接口方法
            interfaceMaker.add(targetClass);
            @SuppressWarnings("unchecked")
            Class<T> targetInterface = interfaceMaker.create();
            return doDynamicProxy(targetInterface, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T, K extends T> T doJdkProxy(Class<T> targetInterface, K target) {
        var targetClass = target.getClass();
        var classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        var interfaces = new Class[]{targetInterface};
        InvocationHandler invocationHandler = new ProxyInvocationHandler<K>(target);

        var proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        var proxyClass = proxyInstance.getClass();
        LOGGER.debug(proxyClass.getName());

        T result;

        try {
            result = targetInterface.cast(proxyInstance);

            return result;
        } catch (Exception e) {
            LOGGER.warn(proxyClass.getName() + " cast to " + targetClass.getName() + " error", e);
            // handle error
            result = target;
        }

        // do after
        LOGGER.debug("after proxy ....");

        return result;
    }

    public <T> T doDynamicProxy(Class<? extends T> targetInterface, T target) {
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) target.getClass();
        var classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        System.out.println(classLoader);
        var interfaces = new Class[]{targetInterface};
        ProxyInvocationHandler<T> invocationHandler = new ProxyInvocationHandler<>(target);

        var proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        var proxyClass = proxyInstance.getClass();
        LOGGER.debug(proxyClass.getName());

        T result;
        if (proxyClass.isInstance(target.getClass())) {
            result = targetClass.cast(proxyInstance);
        } else {
            LOGGER.error(proxyClass.getName() + " cast to " + targetClass.getName() + " error");
            // handle error
            result = target;
        }

        // do after
        LOGGER.debug("after proxy ....");

        return result;
    }

}
