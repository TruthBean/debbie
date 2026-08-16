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

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.debbie.io.ResourceResolver;

import java.util.List;
import java.util.Set;

/**
 * Core context interface for a running Debbie application, providing
 * access to the class loader, environment, bean factory, bean lifecycle
 * listeners, resource resolver, and event publishing.
 *
 * @author TruthBean
 * @since 0.1.0
 */
public interface ApplicationContext extends DebbieEventPublisher {
    /**
     * Returns the command-line arguments parsed at application start.
     *
     * @return application args
     */
    ApplicationArgs getApplicationArgs();

    /** Returns the class loader used for resource and class scanning. */
    ClassLoader getClassLoader();

    /** Returns the holder for all registered environments. */
    EnvironmentDepositoryHolder getEnvironmentHolder();

    /** Returns the default environment. */
    Environment getDefaultEnvironment();

    /** Returns the resource resolver for classpath and filesystem resources. */
    ResourceResolver getResourceResolver();

    /** Returns the bean info manager for registered beans. */
    BeanInfoManager getBeanInfoManager();

    /** Returns the global bean factory for bean instantiation and injection. */
    GlobalBeanFactory getGlobalBeanFactory();

    /** Returns all registered bean lifecycle listeners. */
    Set<BeanLifecycle> getBeanLifecycle();

    /**
     * Transforms the given object to the target type.
     *
     * @param origin the source object
     * @param target the target type
     * @param <O>    the source type
     * @param <T>    the target type
     * @return the transformed value
     */
    <O, T> T transform(final O origin, final Class<T> target);

    /**
     * Releases all context resources (beans, environments, etc.).
     *
     * @param args optional release arguments
     */
    void release(String... args);

    /** Returns whether the application is in the process of exiting. */
    boolean isExiting();
}
