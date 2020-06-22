dependencies {
    api(project(":debbie-core"))

    testImplementation(project(":debbie-test"))

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2:$loggerVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}