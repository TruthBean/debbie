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

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.empty.EmptyApplicationFactory;
import com.truthbean.debbie.internal.DebbieApplicationFactory;
import com.truthbean.debbie.spi.SpiLoader;

import java.util.Collection;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public interface ApplicationFactory {

    ApplicationFactory preInit(String... args);

    /**
     * @param applicationClass application class
     * @param args main method args
     * @return customize life ApplicationFactory
     */
    ApplicationFactory preInit(Class<?> applicationClass, String... args);

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
     * @param beanClasses beans class
     * @return customize life ApplicationFactory
     */
    ApplicationFactory init(Class<?>... beanClasses);

    ApplicationFactory init(ClassLoader classLoader, Class<?>... beanClasses);

    ApplicationFactory register(BeanInfo<?> beanInfo);

    ApplicationFactory register(BeanFactory<?> beanFactory);

    ApplicationFactory register(Collection<BeanInfo<?>> beanInfos);

    ApplicationFactory register(BeanLifecycle beanLifecycle);

    ApplicationFactory register(BeanRegister beanRegister);

    ApplicationFactory config();

    ApplicationFactory config(Object application);

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
        return newEmpty().preInit(applicationClass, args).init().config();
    }

    static ApplicationFactory configure(Object application, String... args) {
        return newEmpty().preInit(application.getClass(), args).init().config(application);
    }

    static ApplicationFactory create(Class<?> applicationClass, String... args) {
        return newEmpty().preInit(applicationClass, args).init().config().create().build();
    }

    static ApplicationFactory create(Object application, String... args) {
        return newEmpty().preInit(application.getClass(), args).init().config(application).create().build();
    }

    static DebbieApplication factory(Class<?> applicationClass, String... args) {
        return newEmpty().preInit(applicationClass, args).init().config().create().postCreate().build().factory();
    }

    static DebbieApplication factory(Object application, String... args) {
        return newEmpty()
                .preInit(application.getClass(), args)
                .init()
                .config(application)
                .create()
                .postCreate()
                .build()
                .factory();
    }
}
