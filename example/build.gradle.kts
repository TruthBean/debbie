apply(plugin = "war")

dependencies {
    compile(project(":debbie-servlet"))
    compile("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
}