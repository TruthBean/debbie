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

    val tomcatVersion: String by project
    api("org.apache.tomcat.embed:tomcat-embed-core:$tomcatVersion")
    api("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatVersion")
    api("org.apache.tomcat.embed:tomcat-embed-el:$tomcatVersion")

    val loggerVersion: String by project
    api("com.truthbean.logger:juli-bridge:$loggerVersion")

    testImplementation(project(":debbie-test"))

    testImplementation("com.truthbean.logger:logger-stdout:$loggerVersion")
}