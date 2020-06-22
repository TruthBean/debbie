dependencies {
    val asmVersion: String by project
    implementation("org.ow2.asm:asm:$asmVersion")

    val jacksonVersion: String by project
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val loggerVersion: String by project
    implementation("com.truthbean.logger:core:$loggerVersion")
}