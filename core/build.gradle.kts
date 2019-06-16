dependencies {
    val asmVersion: String by project
    compile("org.ow2.asm:asm:$asmVersion")

    val jacksonVersion: String by project
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val log4j2Version: String by project
    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}