/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.internal.DebbieApplicationFactory;

import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public interface ApplicationFactory {
    void config(BeanScanConfiguration configuration);

    void release(String... args);

    DebbieApplication factoryApplication();

    ApplicationContext getApplicationContext();

    DebbieApplication postCreateApplication();

    DebbieApplication createApplication(Class<?> applicationClass);

    static DebbieApplication create(Class<?> applicationClass) {
        return DebbieApplicationFactory.create(applicationClass);
    }

    static ApplicationFactory configure(Class<?> applicationClass) {
        return DebbieApplicationFactory.configure(applicationClass);
    }

    static ApplicationFactory configure(ClassLoader classLoader, Consumer<BeanScanConfiguration> consumer) {
        return DebbieApplicationFactory.configure(classLoader, consumer);
    }
}
