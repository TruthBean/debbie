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
    api(project(":debbie-core"))
    api("org.quartz-scheduler:quartz:2.3.2") {
        exclude("org.slf4j", "slf4j-api")
    }

    val slf4jVersion: String by project
    testImplementation("org.slf4j:slf4j-api:$slf4jVersion")

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:slf4j-boot:$loggerVersion")

    val logbackVersion: String by project
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(project(":debbie-test"))
}