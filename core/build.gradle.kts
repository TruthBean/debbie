/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
dependencies {
    // import a BOM. The versions used in this file will override any other version found in the graph
    val truthbeanVersion: String by project
    api(platform("com.truthbean:parent:$truthbeanVersion"))

    api("com.truthbean:logger-core:$truthbeanVersion")
    api("com.truthbean:transformer:$truthbeanVersion")

    api("org.ow2.asm:asm")

    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    // test =========================================================================================================

    testImplementation(project(":debbie-test"))
    testImplementation("com.truthbean:stdout-boot:$truthbeanVersion") {
        exclude(group = "com.truthbean", module = "logger-kotlin")
    }

    testImplementation("org.javassist:javassist")
    testImplementation("cglib:cglib")

    testImplementation("jakarta.inject:jakarta.inject-api")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

}
