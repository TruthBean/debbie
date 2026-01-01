/**
 * Copyright (c) 2025 TruthBean(Rogar·Q)
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
import java.util.function.Function;
import java.util.function.Supplier;

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

    /**
     * init application factory with class loader and into bean class
     * @param classLoader class loader
     * @param beanClasses into bean class
     * @return this instance
     */
    ApplicationFactory init(ClassLoader classLoader, Class<?>... beanClasses);

    /**
     * register custom bean register before bean info register
     * @param beanRegister custom bean register
     * @return this instance
     */
    ApplicationFactory register(BeanRegister beanRegister);

    /**
     * register bean info
     * @param beanInfo bean info
     * @return this instance
     */
    ApplicationFactory register(BeanInfo<?> beanInfo);

    /**
     * register bean infos
     * @param beanInfos bean infos
     * @return this instance
     */
    ApplicationFactory register(Collection<BeanInfo<?>> beanInfos);

    /**
     * register bean infos by function
     * @param beanInfoFunction bean info function
     * @return this instance
     */
    ApplicationFactory register(Function<ApplicationContext, Collection<BeanInfo<?>>> beanInfoFunction);

    /**
     * register bean lifecycle before bean create
     * @param beanLifecycle bean lifecycle
     * @return this instance
     */
    ApplicationFactory register(BeanLifecycle beanLifecycle);

    /**
     * Configure application factory. <br/>
     * Do some configuration before application factory create, <br/>
     * such as register scanned beans, do getComponentAnnotation、registerBean、configure by DebbieModuleStarter, register event listeners etc.
     * @return this instance
     */
    ApplicationFactory config();

    /**
     * Configure application factory with Into Class instance. <br/>
     * Do some configuration before application factory create, <br/>
     * such as register scanned beans, do getComponentAnnotation、registerBean、configure by DebbieModuleStarter, register event listeners etc.
     * @param application Into Class instance
     * @return this instance
     */
    <T> ApplicationFactory config(T application);

    /**
     * Configure application factory with Into Class instance supplier. <br/>
     * Do some configuration before application factory create, <br/>
     * such as register scanned beans, do getComponentAnnotation、registerBean、configure by DebbieModuleStarter, register event listeners etc.
     * @param application Into Class instance supplier
     * @return this instance
     */
    default <T> ApplicationFactory config(Supplier<T> application) {
        return config(application.get());
    }

     /**
     * Configure application factory with bean scan configuration. <br/>
     * Do some configuration before application factory create, <br/>
     * such as register scanned beans, do getComponentAnnotation、registerBean、configure by DebbieModuleStarter, register event listeners etc.
     * @param configuration bean scan configuration
     * @return this instance
     */
    ApplicationFactory config(BeanScanConfiguration configuration);

    /**
     * Create application factory. <br/>
     * Do some configuration after application factory create, such as invoke custom module starter etc.
     * @return this instance
     */
    ApplicationFactory create();

    /**
     * Do some configuration after application factory create
     * @return this instance
     */
    ApplicationFactory postCreate();

    /**
     * build framework application factory.
     * @return this instance
     */
    ApplicationFactory build();

    /**
     * get application context
     * @return application context
     */
    ApplicationContext getApplicationContext();

     /**
     * factory framework application
     * @return framework application instance
     */
    DebbieApplication factory();

     /**
     * release all resources, such as bean info, application context etc.
     */
    void release();

    // ============================================================================================================

    static ApplicationFactory newEmpty() {
        if (DebbieApplication.isDisable()) {
            return SpiLoader.loadProvider(ApplicationFactory.class, new EmptyApplicationFactory());
        }
        return DebbieApplicationFactory.newEmpty();
    }

    static <T> ApplicationFactory initialize(Class<T> applicationClass, String... args) {
        return newEmpty().preInit(applicationClass, args).init();
    }

    static <T> ApplicationFactory configure(Class<T> applicationClass, String... args) {
        return initialize(applicationClass, args).config();
    }

    static <T> ApplicationFactory configure(T application, String... args) {
        return newEmpty().preInit(application.getClass(), args).init().config(application);
    }

    static <T> ApplicationFactory create(Class<T> applicationClass, String... args) {
        return initialize(applicationClass, args).config().create().build();
    }

    static <T> ApplicationFactory create(T application, String... args) {
        return newEmpty().preInit(application.getClass(), args).init().config(application).create().build();
    }

    static <T> DebbieApplication factory(Class<T> applicationClass, String... args) {
        return initialize(applicationClass, args).config().create().postCreate().build().factory();
    }

    static <T> DebbieApplication factory(T application, String... args) {
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
