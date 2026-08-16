/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
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
 * Convenience base class for application factories that pre-initialise
 * the {@link DebbieApplicationFactory} pipeline during construction.
 * <p>
 * Each constructor chains {@code super.preInit(...).init(...)} so that
 * subclasses are ready to proceed with {@code config()} / {@code create()}
 * immediately after instantiation.
 *
 * @author TruthBean
 * @since 0.1.0
 */
public abstract class AbstractApplicationFactory extends DebbieApplicationFactory {

    /**
     * Pre-initialises and initialises the pipeline using the given
     * application class and arguments.
     *
     * @param applicationClass the main application class
     * @param args             command-line arguments
     */
    protected AbstractApplicationFactory(Class<?> applicationClass, String... args) {
        super();
        super.preInit(applicationClass, args).init();
    }

    /**
     * Pre-initialises and initialises the pipeline using the given
     * class loader and arguments.
     *
     * @param classLoader the class loader for resource scanning
     * @param args        command-line arguments
     */
    protected AbstractApplicationFactory(ClassLoader classLoader, String... args) {
        super.preInit(args).init(classLoader);
    }

    /**
     * Pre-initialises and initialises the pipeline using the given
     * application class, class loader and arguments.
     *
     * @param applicationClass the main application class
     * @param classLoader      the class loader for resource scanning
     * @param args             command-line arguments
     */
    public AbstractApplicationFactory(Class<?> applicationClass, ClassLoader classLoader, String... args) {
        super.preInit(applicationClass, args).init(classLoader);
    }
}
