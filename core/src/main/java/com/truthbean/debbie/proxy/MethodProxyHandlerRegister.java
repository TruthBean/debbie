/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import com.truthbean.debbie.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
class MethodProxyHandlerRegister {

    private final Map<Class<? extends Annotation>, List<Class<? extends MethodProxyHandler<? extends Annotation>>>> classListMap;

    private MethodProxyHandlerRegister() {
        classListMap = new LinkedHashMap<>();
        register(MethodProxy.class, DefaultMethodProxyHandler.class);
    }

    private void register(Class<? extends Annotation> annotation,
                         Class<? extends MethodProxyHandler<? extends Annotation>> methodProxyHandler) {
        List<Class<? extends MethodProxyHandler<? extends Annotation>>> classes;

        boolean exits = classListMap.containsKey(annotation);
        if (exits) {
            classes = classListMap.get(annotation);
            if (classes == null) {
                classes = new ArrayList<>();
            }
        } else {
            classes = new ArrayList<>();
        }

        classes.add(methodProxyHandler);
        classListMap.put(annotation, classes);
    }

    @NonNull
    private Map<Class<? extends Annotation>, List<Class<? extends MethodProxyHandler<? extends Annotation>>>> getAllMethodProxyHandlers() {
        return classListMap;
    }

}
