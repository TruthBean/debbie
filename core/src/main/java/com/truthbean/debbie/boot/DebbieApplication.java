/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.internal.DebbieApplicationFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DebbieApplication {

    String SHUTDOWN_HOOK_THREAD_NAME = "DebbieApplicationShutdownHook";

    void start();

    void exit();

    /**
     * get application context
     * same as getApplicationContext
     * @return ApplicationContext
     */
    ApplicationContext getApplicationContext();

    static void run(Class<?> applicationClass, String... args) {
        DebbieApplicationFactory.create(applicationClass, args).start();
    }

    static DebbieApplication create(Class<?> applicationClass, String... args) {
        return DebbieApplicationFactory.create(applicationClass, args);
    }

    static void run(String... args) {
        DebbieApplicationFactory.create(args).start();
    }

    static DebbieApplication create(String... args) {
        return DebbieApplicationFactory.create(args);
    }
}
