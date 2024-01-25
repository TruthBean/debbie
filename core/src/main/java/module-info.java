/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

import com.truthbean.debbie.environment.EnvironmentContext;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
module com.truthbean.debbie.core {
    exports com.truthbean.debbie.annotation;
    exports com.truthbean.debbie.bean;
    exports com.truthbean.debbie.boot;
    exports com.truthbean.debbie.concurrent;
    exports com.truthbean.debbie.core;

    exports com.truthbean.debbie.data;
    exports com.truthbean.debbie.data.validate;
    exports com.truthbean.debbie.data.transformer.text;
    exports com.truthbean.debbie.data.transformer.date;
    exports com.truthbean.debbie.data.transformer.jdbc;
    exports com.truthbean.debbie.data.serialize;

    exports com.truthbean.debbie.event;
    exports com.truthbean.debbie.environment;
    exports com.truthbean.debbie.io;
    exports com.truthbean.debbie.lang;
    exports com.truthbean.debbie.lang.security;

    exports com.truthbean.debbie.net.uri;

    exports com.truthbean.debbie.properties;

    exports com.truthbean.debbie.proxy;
    exports com.truthbean.debbie.proxy.jdk;

    exports com.truthbean.debbie.reflection;
    exports com.truthbean.debbie.spi;
    exports com.truthbean.debbie.task;
    exports com.truthbean.debbie.util;
    exports com.truthbean.debbie.watcher;

    requires java.base;
    requires transitive java.sql;
    requires java.management;

    requires transitive com.truthbean.transformer;
    requires transitive com.truthbean.logger.core;

    exports com.truthbean.debbie.internal to com.truthbean.debbie.servlet, com.truthbean.debbie.tomcat;
    exports com.truthbean.debbie to com.truthbean.debbie.servlet, com.truthbean.debbie.tomcat;

    uses com.truthbean.debbie.boot.DebbieModuleStarter;
    uses com.truthbean.debbie.boot.AbstractApplication;
    uses com.truthbean.debbie.reflection.ExecutableArgumentResolver;
    uses com.truthbean.debbie.task.TaskAction;
    uses com.truthbean.debbie.environment.Environment;
    uses com.truthbean.debbie.environment.EnvironmentSpi;
    uses EnvironmentContext;
    uses com.truthbean.debbie.core.ApplicationFactory;
    uses com.truthbean.debbie.environment.ResourceEnvironment;

    provides com.truthbean.logger.LoggerConfig
            with com.truthbean.debbie.boot.DebbieLoggerConfig;

    provides com.truthbean.debbie.environment.Environment
            with com.truthbean.debbie.properties.PropertiesResourceEnvironment;

    provides com.truthbean.debbie.environment.ResourceEnvironment
            with com.truthbean.debbie.properties.PropertiesResourceEnvironment;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.core.DebbieCoreModuleStarter;
}