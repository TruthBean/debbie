package com.truthbean.debbie.core.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class MethodExecutorFactory {

    public static <T> AbstractMethodExecutor factory(Class<? extends AbstractMethodExecutor> executorClass,
                                                        Class<T> interfaceType, Method method, Object configuration) {
        try {
            var constructor = executorClass.getConstructor(Class.class, Method.class, Object.class);
            return constructor.newInstance(interfaceType, method, configuration);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
