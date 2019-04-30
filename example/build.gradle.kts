// apply(plugin = "war")

dependencies {
    // compile(project(":debbie-tomcat"))
    compile(project(":debbie-netty"))
    compile(project(":debbie-jdbc"))
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
}