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
    // val graalvmVersion: String by project
    // compileOnly("org.graalvm.sdk:graal-sdk:$graalvmVersion")
    val asmVersion: String by project
    compileOnly("org.ow2.asm:asm:$asmVersion")

    val javassistVersion: String by project
    compileOnly("org.javassist:javassist:$javassistVersion")

    val jacksonVersion: String by project
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    // test =========================================================================================================

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2:$loggerVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    testImplementation(project(":debbie-test"))
    // testImplementation(project(":debbie-aio"))

    testImplementation("org.ow2.asm:asm:$asmVersion")
    testImplementation("org.javassist:javassist:$javassistVersion")
    val cglibVersion: String by project
    testImplementation("cglib:cglib:$cglibVersion")

    val injectVersion: String by project
    testImplementation("javax.inject:javax.inject:$injectVersion")
}