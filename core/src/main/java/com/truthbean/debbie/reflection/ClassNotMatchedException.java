/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/18 23:10.
 */
public class ClassNotMatchedException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 3849437820066752549L;

    public ClassNotMatchedException(Class<?> expectedClass, Class<?> clazz) {
        super(clazz.getCanonicalName() + " not matched " + expectedClass.getCanonicalName());
    }
}
