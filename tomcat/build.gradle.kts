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

/*extraJavaModuleInfo {
    val tomcatVersion: String by project
    automaticModule("tomcat-embed-core-${tomcatVersion}.jar", "org.apache.tomcat.embed.core")
    automaticModule("tomcat-embed-jasper-${tomcatVersion}.jar", "org.apache.tomcat.embed.jasper")
    automaticModule("tomcat-embed-el-${tomcatVersion}.jar", "org.apache.tomcat.embed.el")
    automaticModule("tomcat-annotations-api-${tomcatVersion}.jar", "org.apache.tomcat.annotations.api")
    automaticModule("ecj-3.18.0.jar", "org.eclipse.jdt.ecj")
}*/

dependencies {
    api(project(":debbie-servlet"))
    api(project(":debbie-server"))

    val tomcatVersion: String by project
    api("org.apache.tomcat.embed:tomcat-embed-core:$tomcatVersion")
    api("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatVersion")
    api("org.apache.tomcat.embed:tomcat-embed-el:$tomcatVersion")

    val loggerVersion: String by project
    api("com.truthbean.logger:juli-impl:$loggerVersion")

    testImplementation(project(":debbie-test"))

    testImplementation("com.truthbean.logger:stdout-adapter:$loggerVersion")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}