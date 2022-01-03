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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.transformer.DataTransformer;
import com.truthbean.transformer.DataTransformerCenter;
import com.truthbean.common.mini.util.ReflectionUtils;
import com.truthbean.transformer.Transformer;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author TruthBean
 * @since 0.0.1
 */
class DataTransformerFactory {

    private DataTransformerFactory() {
    }

    public static void register(Collection<Class<?>> classes) {
        if (classes != null && !classes.isEmpty()) {
            for (Class<?> clazz : classes) {
                registerDataTransformer(clazz);
            }
        }
    }

    public static <DT> void registerDataTransformer(Class<DT> transformerClass) {
        var support = transformerClass != null && !transformerClass.isInterface()
                && !transformerClass.isAnnotation() && !transformerClass.isEnum()
                && !Modifier.isAbstract(transformerClass.getModifiers())
                && DataTransformer.class.isAssignableFrom(transformerClass)
                && transformerClass.getAnnotation(Transformer.class) != null;
        if (support) {
            DT transformer = ReflectionUtils.newInstance(transformerClass);
            Type[] argsType = ReflectionUtils.getActualTypes(transformerClass);
            DataTransformerCenter.register((DataTransformer<?, ?>) transformer, argsType);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerFactory.class);
}
