dependencies {
    val asmVersion: String by project
    implementation("org.ow2.asm:asm:$asmVersion")

    val javassistVersion: String by project
    implementation("org.javassist:javassist:$javassistVersion")

    val jacksonVersion: String by project
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val loggerVersion: String by project
    implementation("com.truthbean.logger:core:$loggerVersion")
    implementation("com.truthbean.logger:log4j2:$loggerVersion")

    val slf4jVersion: String by project
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    val log4j2Version: String by project
    implementation("org.apache.logging.log4j:log4j-core:$log4j2Version")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}