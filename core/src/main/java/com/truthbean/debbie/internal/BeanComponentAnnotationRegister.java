/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-06 22:20
 */
class BeanComponentAnnotationRegister {
    private static final Object object = new Object();
    private static final Map<Class<? extends Annotation>, Object> annotations = new ConcurrentHashMap<>();

    public static <A extends Annotation> void register(Class<A> annotation) {
        annotations.put(annotation, object);
    }

    public static Set<Class<? extends Annotation>> getBeanComponentAnnotation() {
        return annotations.keySet();
    }

    public static void reset() {
        annotations.clear();
    }
}
