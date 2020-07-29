/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.reflection;

import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * &lt;T&gt; method return type
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-20 14:28.
 */
public class MethodHandler<T, R> {

    private MethodHandle methodHandle;
    private Class<R> rawReturnType;

    /**
     * 使用Java7的新api，MethodHandle
     * invoke virtual 动态绑定后调用 obj.xxx
     * invoke special 静态绑定后调用 super.xxx
     *
     * @return MethodHandler
     */
    public static <T, R> MethodHandler<T, R> getMethodHandle(Class<T> declareClass, Class<R> returnType, String methodName,
                                                             Class<?>... paramType) {
        MethodHandle methodHandle = null;
        MethodType desc = MethodType.methodType(returnType, paramType);
        try {
            methodHandle = MethodHandles.lookup().findVirtual(declareClass, methodName, desc);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LOGGER.error("", e);
            return null;
        }
        MethodHandler<T, R> methodHandler = new MethodHandler<>();
        methodHandler.methodHandle = methodHandle;
        methodHandler.rawReturnType = returnType;
        return methodHandler;
    }

    public static MethodHandler getMethodHandle(Method method) {
        MethodHandle methodHandle = null;
        try {
            methodHandle = MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
            return null;
        }
        MethodHandler methodHandler = new MethodHandler<>();
        methodHandler.methodHandle = methodHandle;
        methodHandler.rawReturnType = method.getReturnType();
        return methodHandler;
    }

    /**
     * 不要使用！！！
     * @param instance
     * @param args
     * @return
     */
    public R invokeExact(T instance, Object... args) {
        try {
            if (rawReturnType != null && rawReturnType != void.class && rawReturnType != Void.class) {
                return (R) methodHandle.invoke((T) instance, args);
            } else {
                if (args != null && args.length > 0) {
                    if (args.length == 1)
                        methodHandle.invoke((T) instance, args[0]);
                } else {
                    methodHandle.invoke((T) instance);
                }
            }
        } catch (Throwable throwable) {
            LOGGER.error("", throwable);
        }
        return null;
    }

    public MethodHandle getMethodHandle() {
        return methodHandle;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandler.class);
}
