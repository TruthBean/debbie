plugins {
    id("de.jjohannes.extra-java-module-info") version "0.1"
}

extraJavaModuleInfo {
    module("jakarta.inject-api-1.0.jar", "jakarta.inject.api", "1.0")
}
dependencies {
    api(project(":debbie-core"))

    val asmVersion: String by project
    implementation("org.ow2.asm:asm:$asmVersion")

    val jacksonVersion: String by project
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val loggerVersion: String by project
    implementation("com.truthbean.logger:log4j2-boot:$loggerVersion")

    val slf4jVersion: String by project
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
}