dependencies {
    api(project(":debbie-core"))

    val slf4jVersion: String by project
    "compileOnly"("org.slf4j:slf4j-api:$slf4jVersion")

    val jupiterVersion: String by project
    "implementation"("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    "implementation"("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
    "implementation"("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}