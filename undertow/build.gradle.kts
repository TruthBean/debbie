dependencies {
    compile(project(":debbie-mvc"))
    compile(project(":debbie-server"))

    val undertowVersion: String by project
    compile("io.undertow:undertow-core:$undertowVersion")

    val log4j2Version: String by project
    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")
}