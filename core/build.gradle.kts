dependencies {
    val asmVersion: String by project
    compile("org.ow2.asm:asm:$asmVersion")

    val jacksonVersion: String by project
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val log4j2Version: String by project
    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    testCompile(project(":debbie-test"))

    testImplementation("org.javassist:javassist:3.26.0-GA")
    testCompile("cglib:cglib:3.3.0")
}