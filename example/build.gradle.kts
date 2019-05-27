// apply(plugin = "war")

dependencies {
    // compile(project(":debbie-undertow"))
    compile(project(":debbie-tomcat"))
    // compile(project(":debbie-netty"))

    compile(project(":debbie-httpclient"))

    compile(project(":debbie-jdbc"))

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")

    compile("org.apache.commons:commons-lang3:3.9")
}