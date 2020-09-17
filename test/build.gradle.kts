dependencies {
    implementation(project(":debbie-core"))

    val jupiterVersion: String by project
    api("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    api("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
    api("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2-adapter:$loggerVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}