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

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class ClassTransformer implements DataTransformer<Class, String> {
    @Override
    public String transform(Class aClass) {
        return aClass.getName();
    }

    @Override
    public Class reverse(String s) {
        ClassLoader defaultClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        try {
            return defaultClassLoader.loadClass(s);
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassTransformer.class);
}
