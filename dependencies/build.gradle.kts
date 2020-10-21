/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
plugins {
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}
dependencyManagement {
    dependencies {
        // core-main
        val asmVersion: String by project
        dependency("org.ow2.asm:asm:$asmVersion")
        val jacksonVersion: String by project
        dependency("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
        dependency("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
        dependency("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
        dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
        dependency("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
        dependency("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
        dependency("com.fasterxml.jackson.module:jackson-module-afterburner:$jacksonVersion")
        dependency("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:$jacksonVersion")
        val snakeyamlVersion: String by project
        dependency("org.yaml:snakeyaml:$snakeyamlVersion")
        val slf4jVersion: String by project
        dependency("org.slf4j:slf4j-api:$slf4jVersion")

        val injectVersion: String by project
        dependency("jakarta.inject:jakarta.inject-api:$injectVersion")

        // core-test
        val jupiterVersion: String by project
        dependency("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
        dependency("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
        dependency("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")

        // class
        val javassistVersion: String by project
        dependency("org.javassist:javassist:$javassistVersion")
        val cglibVersion: String by project
        dependency("cglib:cglib:$cglibVersion")

        // logs
        val loggerVersion: String by project
        dependency("com.truthbean.logger:logger-core:${loggerVersion}")
        dependency("com.truthbean.logger:jdk-adapter:${loggerVersion}")
        dependency("com.truthbean.logger:log4j-adapter:${loggerVersion}")
        dependency("com.truthbean.logger:log4j2-adapter:${loggerVersion}")
        dependency("com.truthbean.logger:slf4j-adapter:${loggerVersion}")
        dependency("com.truthbean.logger:stdout-adapter:${loggerVersion}")
        dependency("com.truthbean.logger:log4j2-boot:${loggerVersion}")
        dependency("com.truthbean.logger:slf4j-boot:${loggerVersion}")
        val log4j2Version: String by project
        dependency("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")
        dependency("org.apache.logging.log4j:log4j-api:$log4j2Version")
        dependency("org.apache.logging.log4j:log4j-core:$log4j2Version")
        val logbackVersion: String by project
        dependency("ch.qos.logback:logback-classic:$logbackVersion") {
            exclude("org.slf4j:slf4j-api")
        }

        // jdbc-test
        val mysqlVersion: String by project
        dependency("mysql:mysql-connector-java:$mysqlVersion")
        val mariadbVersion: String by project
        dependency("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")
        val postgresqlVersion: String by project
        dependency("org.postgresql:postgresql:$postgresqlVersion")
        val sqliteVersion: String by project
        dependency("org.xerial:sqlite-jdbc:$sqliteVersion")

        // hikari
        val hikariVersion: String by project
        dependency("com.zaxxer:HikariCP:$hikariVersion")

        // servlet-main
        val servletVersion: String by project
        dependency("jakarta.servlet:jakarta.servlet-api:$servletVersion")
        val jstlVersion: String by project
        dependency("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:$jstlVersion")
        dependency("org.glassfish.web:javax.servlet.jsp.jstl:$jstlVersion") {
            exclude("javax.servlet:servlet-api")
            exclude("javax.servlet.jsp:jsp-api")
            exclude("javax.servlet.jsp.jstl:jstl-api")
        }
        dependency("org.apache.taglibs:taglibs-standard-jstlel:$jstlVersion")

        val fileuploadVersion: String by project
        dependency("commons-fileupload:commons-fileupload:$fileuploadVersion") {
            exclude("javax.servlet:servlet-api")
            exclude("portlet-api:portlet-api")
        }

        val commonsIoVersion: String by project
        dependency("commons-io:commons-io:${commonsIoVersion}")

        // tomcat
        val tomcatVersion: String by project
        dependency("org.apache.tomcat.embed:tomcat-embed-core:$tomcatVersion")
        dependency("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatVersion")
        dependency("org.apache.tomcat.embed:tomcat-embed-el:$tomcatVersion")

        // undertow
        val undertowVersion: String by project
        dependency("io.undertow:undertow-core:$undertowVersion")

        // netty
        val nettyVersion: String by project
        imports {
            mavenBom("io.netty:netty-bom:$nettyVersion")
        }
        /*dependency("io.netty:netty-codec-http2:$nettyVersion")
        dependency("io.netty:netty-transport-native-epoll:$nettyVersion")
        dependency("io.netty:netty-transport-native-kqueue:$nettyVersion")
        dependency("io.netty:netty-transport-native-epoll:$nettyVersion")
        dependency("io.netty:netty-transport-native-kqueue:$nettyVersion")
        dependency("io.netty:netty-resolver-dns:$nettyVersion")
        dependency("io.netty:netty-handler-proxy:$nettyVersion")
        dependency("io.netty:netty-codec-mqtt:$nettyVersion")
        dependency("io.netty:netty-transport-rxtx:$nettyVersion")
        dependency("io.netty:netty-codec-stomp:$nettyVersion")
        dependency("io.netty:netty-transport-sctp:$nettyVersion")
        dependency("io.netty:netty-transport-udt:$nettyVersion")
        dependency("io.netty:netty-codec-haproxy:$nettyVersion")
        dependency("io.netty:netty-codec-redis:$nettyVersion")
        dependency("io.netty:netty-codec-memcache:$nettyVersion")
        dependency("io.netty:netty-codec-smtp:$nettyVersion")*/

        // debbie
        val projectVersion: String by project
        dependency("com.truthbean.debbie:debbie-core:$projectVersion")
        dependency("com.truthbean.debbie:debbie-cron:$projectVersion")
        dependency("com.truthbean.debbie:debbie-jdbc:$projectVersion")
        dependency("com.truthbean.debbie:debbie-mvc:$projectVersion")
        dependency("com.truthbean.debbie:debbie-rmi:$projectVersion")
        dependency("com.truthbean.debbie:debbie-servlet:$projectVersion")
        dependency("com.truthbean.debbie:debbie-httpclient:$projectVersion")
        dependency("com.truthbean.debbie:debbie-hikari:$projectVersion")
        dependency("com.truthbean.debbie:debbie-server:$projectVersion")
        dependency("com.truthbean.debbie:debbie-tomcat:$projectVersion")
        dependency("com.truthbean.debbie:debbie-undertow:$projectVersion")
        dependency("com.truthbean.debbie:debbie-netty:$projectVersion")

        dependency("com.truthbean.debbie:debbie-aio:$projectVersion")
        dependency("com.truthbean.debbie:debbie-test:$projectVersion")

        // extra

        // mybatis
        val mybatisVersion: String by project
        dependency("org.mybatis:mybatis:$mybatisVersion")

        dependency("com.truthbean.debbie:debbie-mybatis:$projectVersion")

        // swagger
        val swaggerUiVersion: String by project
        dependency("org.webjars:swagger-ui:$swaggerUiVersion")

        val swaggerVersion: String by project
        dependency("io.swagger.core.v3:swagger-integration:$swaggerVersion")
        dependency("io.swagger.core.v3:swagger-core:$swaggerVersion")
        dependency("io.swagger.core.v3:swagger-models:$swaggerVersion")
        dependency("io.swagger.core.v3:swagger-annotations:$swaggerVersion")

        dependency("com.truthbean.debbie:debbie-swagger:$projectVersion")

        // freemarker
        val freemarkerVersion: String by project
        dependency("org.freemarker:freemarker:$freemarkerVersion")

        dependency("com.truthbean.debbie:debbie-freemarker:$projectVersion")

        // spring
        val springVersion: String by project
        dependency("org.springframework:spring-context-support:${springVersion}")
        dependency("com.truthbean.debbie:debbie-spring:$projectVersion")

        val okhttpVersion: String by project
        dependency("com.squareup.okhttp3:okhttp:$okhttpVersion")

        val commonsCodesVersion: String by project
        dependency("commons-codec:commons-codec:$commonsCodesVersion")
    }
}