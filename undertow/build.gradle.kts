/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/*plugins {
    id("de.jjohannes.extra-java-module-info") version "0.1"
}*/

dependencies {
    api(project(":debbie-server"))

    val undertowVersion: String by project
    api("io.undertow:undertow-core:$undertowVersion")
    api("org.jboss.logging:jboss-logging-annotations:2.2.1.Final")

    testImplementation(project(":debbie-test"))

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2-adapter:$loggerVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}

/*
extraJavaModuleInfo {
    val undertowVersion: String by project
    module("undertow-core-${undertowVersion}.jar", "io.undertow.core", undertowVersion) {
        exports("io.undertow")
        exports("io.undertow.attribute")
        exports("io.undertow.channels")
        exports("io.undertow.client")
        exports("io.undertow.conduits")
        exports("io.undertow.connector")
        exports("io.undertow.io")
        exports("io.undertow.predicate")
        // exports("io.undertow.protocols")
        // exports("io.undertow.protocols")
        exports("io.undertow.server")
        exports("io.undertow.server.session")
        exports("io.undertow.server.handlers")
        exports("io.undertow.server.handlers.form")
        exports("io.undertow.util")
        exports("io.undertow.websockets")
        requires("org.jboss.logging")
        requires("org.xnio.api")
    }
    module("jboss-logging-3.4.1.Final.jar", "org.jboss.logging", "3.4.1") {
        exports("org.jboss.logging")
        requires("java.logging")
    }
    module("jboss-threads-3.1.0.Final.jar", "org.jboss.threads", "3.1.0")
    module("xnio-api-3.8.0.Final.jar", "org.xnio.api", "3.8.0") {
        exports("org.xnio")
        requires("org.jboss.logging")
        // uses("org.xnio.XnioProvider")
    }
    module("xnio-nio-3.8.0.Final.jar", "org.xnio.nio", "3.8.0")
    module("wildfly-client-config-1.0.1.Final.jar", "org.wildfly.client.config", "1.0.1")
    module("wildfly-common-1.5.2.Final.jar", "org.wildfly.common", "1.5.2")
}*/
