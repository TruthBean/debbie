/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.core;

import com.truthbean.debbie.internal.DebbieApplicationFactory;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public abstract class AbstractApplicationFactory extends DebbieApplicationFactory {

    protected AbstractApplicationFactory(Class<?> applicationClass, String... args) {
        super();
        super.preInit(applicationClass, args).init();
    }

    protected AbstractApplicationFactory(ClassLoader classLoader, String... args) {
        super.preInit(args).init(classLoader);
    }

    public AbstractApplicationFactory(Class<?> applicationClass, ClassLoader classLoader, String... args) {
        super.preInit(applicationClass, args).init(classLoader);
    }
}
