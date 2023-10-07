/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
dependencies {
    api(project(":debbie-core"))

    val truthbeanVersion: String by project

// ====================================================================================================================

    testImplementation("com.truthbean:truthbean-log4j2-boot:$truthbeanVersion")

    testImplementation("mysql:mysql-connector-java")

    // testCompile(project(":debbie-hikari"))
    testImplementation(project(":debbie-test"))
    testImplementation(project(":debbie-asm"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.mariadb.jdbc:mariadb-java-client")

    // todo: sqlite3 postgresql oracle h2 等
    testImplementation("org.xerial:sqlite-jdbc")
    testImplementation("com.h2database:h2")
}
