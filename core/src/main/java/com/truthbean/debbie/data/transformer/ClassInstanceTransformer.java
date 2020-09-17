/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer;

import com.truthbean.Logger;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.logger.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class ClassInstanceTransformer implements DataTransformer<Object, String> {
    @Override
    public String transform(Object o) {
        return o.getClass().getName();
    }

    @Override
    public Object reverse(String className) {
        var defaultClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        Class<?> clazz = null;
        try {
            clazz = defaultClassLoader.loadClass(className);
            return ReflectionHelper.newInstance(clazz);
        } catch (ClassNotFoundException e) {
            logger.error("", e);
        }
        return null;
    }

    private static final Logger logger = LoggerFactory.getLogger(ClassInstanceTransformer.class);
}
