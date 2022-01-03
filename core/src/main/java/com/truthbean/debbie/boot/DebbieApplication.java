/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.common.mini.util.AbstractPropertiesUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.empty.EmptyApplicationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.logger.LoggerConfig;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DebbieApplication {

    String SHUTDOWN_HOOK_THREAD_NAME = "DebbieApplicationShutdownHook";

    String DISABLE_DEBBIE = "com.truthbean.debbie.disable";

    void start();

    void exit();

    /**
     * get application context
     * same as getApplicationContext
     *
     * @return ApplicationContext
     */
    ApplicationContext getApplicationContext();

    static boolean isDisable() {
        var bool = AbstractPropertiesUtils.isSysTrue(DISABLE_DEBBIE);
        if (bool) {
            System.setProperty(LoggerConfig.STD_OUT, "true");
            Logger logger = LoggerFactory.getLogger(DebbieApplication.class);
            logger.info(DISABLE_DEBBIE + ":true");
            logger.info("debbie is disabled by you!");
        }
        return bool;
    }

    static void run(Class<?> applicationClass, String... args) {
        ApplicationFactory applicationFactory;
        if (isDisable()) {
            applicationFactory = SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        } else {
            applicationFactory = ApplicationFactory.newEmpty();
        }
        applicationFactory
                .preInit(applicationClass, args)
                .init()
                .config()
                .create()
                .postCreate()
                .build()
                .factory()
                .start();
    }

    static DebbieApplication create(Class<?> applicationClass, String... args) {
        ApplicationFactory applicationFactory;
        if (isDisable()) {
            applicationFactory = SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        } else {
            applicationFactory = ApplicationFactory.newEmpty();
        }
        return applicationFactory
                .preInit(applicationClass, args)
                .init()
                .config()
                .create()
                .postCreate()
                .build()
                .factory();
    }

    static void run(Object application, String... args) {
        ApplicationFactory applicationFactory;
        if (isDisable()) {
            applicationFactory = SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        } else {
            applicationFactory = ApplicationFactory.newEmpty();
        }
        applicationFactory
                .preInit(application.getClass(), args)
                .init(ClassLoaderUtils.getClassLoader(application.getClass()))
                .config(application)
                .create()
                .postCreate()
                .build()
                .factory()
                .start();
    }

    static DebbieApplication create(Object application, String... args) {
        ApplicationFactory applicationFactory;
        if (isDisable()) {
            applicationFactory = SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        } else {
            applicationFactory = ApplicationFactory.newEmpty();
        }
        return applicationFactory
                .preInit(application.getClass(), args)
                .init()
                .config(application)
                .create()
                .postCreate()
                .build()
                .factory();
    }

    static void run(String... args) {
        ApplicationFactory applicationFactory;
        if (isDisable()) {
            applicationFactory = SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        } else {
            applicationFactory = ApplicationFactory.newEmpty();
        }
        applicationFactory
                .preInit(args)
                .init()
                .config()
                .create()
                .postCreate()
                .build()
                .factory()
                .start();
    }

    static DebbieApplication create(String... args) {
        ApplicationFactory applicationFactory;
        if (isDisable()) {
            applicationFactory = SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        } else {
            applicationFactory = ApplicationFactory.newEmpty();
        }
        return applicationFactory
                .preInit(args)
                .init()
                .config()
                .create()
                .postCreate()
                .build()
                .factory();
    }
}
