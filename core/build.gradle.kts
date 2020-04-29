dependencies {
    val asmVersion: String by project
    implementation("org.ow2.asm:asm:$asmVersion")

    val jacksonVersion: String by project
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    testImplementation(project(":debbie-test"))
    testImplementation(project(":debbie-aio"))

    val javassistVersion: String by project
    testImplementation("org.javassist:javassist:$javassistVersion")
    val cglibVersion: String by project
    testImplementation("cglib:cglib:$cglibVersion")

    val injectVersion: String by project
    testImplementation("javax.inject:javax.inject:$injectVersion")
}