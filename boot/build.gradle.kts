dependencies {
    api(project(":debbie-core"))

    val asmVersion: String by project
    implementation("org.ow2.asm:asm:$asmVersion")

    /*val javassistVersion: String by project
    implementation("org.javassist:javassist:$javassistVersion")*/

    val jacksonVersion: String by project
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val loggerVersion: String by project
    implementation("com.truthbean.logger:log4j2-boot:$loggerVersion")

    val slf4jVersion: String by project
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
}