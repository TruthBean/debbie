/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
dependencies {
    api(project(":debbie-servlet"))
    api(project(":debbie-server"))
    api(project(":debbie-boot"))

    val tomcatVersion: String by project
    api("org.apache.tomcat.embed:tomcat-embed-core:$tomcatVersion")
    api("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatVersion")
    api("org.apache.tomcat.embed:tomcat-embed-el:$tomcatVersion")

    testImplementation(project(":debbie-test"))

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2:$loggerVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}