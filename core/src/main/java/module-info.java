/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
module com.truthbean.debbie.core {
    exports com.truthbean.debbie.bean;
    exports com.truthbean.debbie.boot;
    exports com.truthbean.debbie.concurrent;
    exports com.truthbean.debbie.core;

    exports com.truthbean.debbie.data;
    exports com.truthbean.debbie.data.validate;
    exports com.truthbean.debbie.data.transformer;
    exports com.truthbean.debbie.data.transformer.text;
    exports com.truthbean.debbie.data.transformer.collection;
    exports com.truthbean.debbie.data.transformer.date;
    exports com.truthbean.debbie.data.transformer.jdbc;
    exports com.truthbean.debbie.data.transformer.numeric;

    exports com.truthbean.debbie.event;
    exports com.truthbean.debbie.io;
    exports com.truthbean.debbie.lang;

    exports com.truthbean.debbie.net;
    exports com.truthbean.debbie.net.uri;

    exports com.truthbean.debbie.properties;

    exports com.truthbean.debbie.proxy;
    exports com.truthbean.debbie.proxy.jdk;

    exports com.truthbean.debbie.reflection;
    exports com.truthbean.debbie.spi;
    exports com.truthbean.debbie.task;
    exports com.truthbean.debbie.util;
    exports com.truthbean.debbie.watcher;
    exports com.truthbean.debbie.proxy.asm;

    requires java.base;
    requires transitive java.sql;
    requires java.management;

    requires transitive com.truthbean.logger.core;
    requires org.objectweb.asm;
    // requires javassist;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.dataformat.yaml;

    exports com.truthbean.debbie.internal to com.truthbean.debbie.servlet;

    uses com.truthbean.debbie.boot.DebbieModuleStarter;
    uses com.truthbean.debbie.boot.AbstractApplication;
    uses com.truthbean.debbie.reflection.ExecutableArgumentResolver;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.core.DebbieCoreModuleStarter;
}