dependencies {
    compile(project(":debbie-mvc"))

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    testCompile(project(":debbie-undertow"))
}