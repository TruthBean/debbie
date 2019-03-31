// apply(plugin = "war")

dependencies {
    compile(project(":debbie-tomcat"))
    compile("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
}