package com.truthbean.debbie.proxy.interfaces;

import com.truthbean.debbie.proxy.*;
import com.truthbean.debbie.proxy.asm.AsmProxy;
import com.truthbean.debbie.proxy.bean.TargetImplement;
import com.truthbean.debbie.proxy.bean.TargetInterface;

import com.truthbean.debbie.proxy.bean.TargetObject;
import com.truthbean.debbie.proxy.interfaces.cglib.TargetMethodInterceptor;
import com.truthbean.debbie.proxy.interfaces.jdk.JdkInvocationHandler;
import com.truthbean.debbie.reflection.ByteArrayClassLoader;
import com.truthbean.debbie.reflection.ReflectionHelper;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class TargetTest {

    @Test
    public void direct() throws Exception {
        TargetProxyHandler interceptor = new TargetProxyHandler();
        List<MethodProxyHandler> list = List.of(interceptor);
        TargetObject target = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);

        var proxy = new MethodProxyHandlerHandler(LOGGER);
        proxy.setInterceptors(list);

        MethodCallBack<Void> callBack = new MethodCallBack<Void>(target, "code", new Class[]{String.class}, new Object[]{"java"});

        proxy.proxy(callBack);
    }

    @Test
    public void jdkProxy() {
        TargetInterface target = new TargetImplement();
        JdkInvocationHandler<TargetInterface> handler = new JdkInvocationHandler<>(TargetInterface.class, target);
        TargetInterface proxy = handler.newProxyInstance();
        proxy.coding();
    }

    @Test
    public void cglibProxy() throws Exception {
        Enhancer enhancer = new Enhancer();
        TargetObject target = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);
        enhancer.setCallback(new TargetMethodInterceptor(target, new TargetProxyHandler(), LOGGER));
        // enhancer.setInterfaces(new Class[]{TargetInterface.class});
        enhancer.setSuperclass(TargetObject.class);
        TargetObject proxy = (TargetObject) enhancer.create();
        proxy.code("c++");
    }

    @Test
    public void javassistProxy() throws Exception {
        TargetProxyHandler interceptor = new TargetProxyHandler();
        MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
        handler.setInterceptors(List.of(interceptor));

        TargetObject targetBean = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);
        JavassistProxy<TargetObject> javassistProxy = new JavassistProxy<>(TargetObject.class, handler, MethodProxy.class);
        TargetObject proxy = javassistProxy.proxy(targetBean);
        proxy.learn();
        proxy.coding();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

        proxy.code("java");
    }

    @Test
    void testAsmProxy() throws Exception {
        TargetObject targetBean = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);
        // TargetInterface targetBean = new TargetImplement();
        TargetProxyHandler interceptor = new TargetProxyHandler();
        MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
        handler.setInterceptors(List.of(interceptor));

        AsmProxy<TargetObject> asmProxy = new AsmProxy<>(TargetObject.class, handler, MethodProxy.class);
        TargetObject proxy = asmProxy.proxy(targetBean);

        Set<Method> proxyMethod = asmProxy.getProxyMethods();
        for (Method method : proxyMethod) {
            System.out.println(method.getName());
        }

        proxy.learn();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

        proxy.code("rust");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TargetTest.class);
}