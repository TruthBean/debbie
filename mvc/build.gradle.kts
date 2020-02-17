dependencies {
    api(project(":debbie-core"))

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    testImplementation(project(":debbie-undertow"))
    testImplementation(project(":debbie-test"))
}