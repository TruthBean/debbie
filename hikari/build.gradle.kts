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
    api(project(":debbie-jdbc"))
    api(project(":debbie-metrics"))

    val slf4jVersion: String by project
    runtimeOnly("org.slf4j:slf4j-api:$slf4jVersion")

    val hikariVersion: String by project
    api("com.zaxxer:HikariCP:$hikariVersion") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2-boot:$loggerVersion")

    val mysqlVersion: String by project
    testImplementation("mysql:mysql-connector-java:$mysqlVersion")
    
    val mariadbVersion: String by project
    testImplementation("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")

    testImplementation(project(":debbie-test"))
}