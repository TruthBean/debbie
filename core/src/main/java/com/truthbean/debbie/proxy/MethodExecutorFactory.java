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

import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class MethodExecutorFactory {

    public static <T> AbstractMethodExecutor factory(Class<? extends AbstractMethodExecutor> executorClass,
                                                        Class<T> interfaceType, Method method, Object configuration) {
        return ReflectionHelper.newInstance(executorClass,
                new Class[]{Class.class, Method.class, Object.class},
                new Object[]{interfaceType, method, configuration});
    }

}
