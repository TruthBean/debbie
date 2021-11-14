/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.core;

import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.empty.EmptyApplicationFactory;
import com.truthbean.debbie.internal.DebbieApplicationFactory;
import com.truthbean.debbie.spi.SpiLoader;

import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public interface ApplicationFactory {

    ApplicationFactory preInit(String... args);

    /**
     * register module starter before configure or create
     *
     * @param moduleStarter debbie module starter
     * @return this instance
     */
    ApplicationFactory registerModuleStarter(DebbieModuleStarter moduleStarter);

    /**
     * configure properties and register beans
     *
     * @return customize life ApplicationFactory
     */
    ApplicationFactory init();

    /**
     * configure properties and register beans
     *
     * @param applicationClass main class which annotated by DebbieBootApplication
     * @return customize life ApplicationFactory
     */
    ApplicationFactory init(Class<?> applicationClass);

    ApplicationFactory init(ClassLoader classLoader);

    ApplicationFactory init(Class<?> applicationClass, ClassLoader classLoader);

    ApplicationFactory config();

    ApplicationFactory config(BeanScanConfiguration configuration);

    ApplicationFactory create();

    ApplicationFactory postCreate();

    ApplicationFactory build();

    ApplicationContext getApplicationContext();

    DebbieApplication factory();

    void release();

    // ============================================================================================================

    static ApplicationFactory newEmpty() {
        if (DebbieApplication.isDisable()) {
            return SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        }
        return DebbieApplicationFactory.newEmpty();
    }

    static ApplicationFactory configure(Class<?> applicationClass, String... args) {
        return newEmpty().preInit(args).init(applicationClass).config();
    }

    static ApplicationFactory create(Class<?> applicationClass, String... args) {
        return newEmpty().preInit(args).init(applicationClass).config().create().build();
    }

    static DebbieApplication factory(Class<?> applicationClass, String... args) {
        return newEmpty().preInit(args).init(applicationClass).config().create().postCreate().build().factory();
    }
}
