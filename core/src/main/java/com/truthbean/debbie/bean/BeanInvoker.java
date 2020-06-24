/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 12:05.
 */
public class BeanInvoker<Bean> {
    private final Class<Bean> beanClass;
    private final DebbieBeanInfo<Bean> beanInfo;
    private final Map<String, Method> beanMethods = new HashMap<>();

    private Method initMethod;
    private Bean bean;

    private BeanCreator<Bean> beanCreator;

    public BeanInvoker(Class<Bean> beanClass, BeanFactoryHandler beanFactoryHandler) {
        BeanInitialization initialization = beanFactoryHandler.getBeanInitialization();
        this.beanClass = beanClass;
        this.beanInfo = initialization.getRegisterRawBean(beanClass);

        var methods = initialization.getBeanMethods(beanClass);
        setMethods(methods);

        beanCreator = new BeanCreatorImpl<>(beanClass, beanFactoryHandler);
    }

    public BeanInvoker(DebbieBeanInfo<Bean> beanInfo, BeanFactoryHandler beanFactoryHandler) {
        this.beanClass = beanInfo.getBeanClass();
        this.beanInfo = beanInfo;

        var methods = beanInfo.getMethods();
        setMethods(methods);

        beanCreator = new BeanCreatorImpl<>(beanInfo, beanFactoryHandler);
    }

    private void setMethods(Set<Method> methods) {
        methods.forEach(method -> {
            beanMethods.put(method.getName(), method);
            if (method.getAnnotation(BeanInit.class) != null) {
                initMethod = method;
            }
        });
    }

    public Bean getBean() {
        return bean;
    }

    public DebbieBeanInfo<Bean> getBeanInfo() {
        return beanInfo;
    }

    public Object invokeMethod(String methodName, Object[] parameters) {
        var beanMethod = beanMethods.get(methodName);
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Class<? extends Annotation> methodAnnotation, String methodName, Object[] parameters) {
        var beanMethod = beanMethods.get(methodName);
        // TODO: 2019-11-30  methodAnnotation ???
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Method routerMethod, Object[] parameters) {
        return ReflectionHelper.invokeMethod(bean, routerMethod, parameters);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanInvoker.class);
}
