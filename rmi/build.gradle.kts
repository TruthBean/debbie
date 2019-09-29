dependencies {
    compile(project(":debbie-core"))

    val log4j2Version: String by project
    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}