dependencies {
    api(project(":debbie-core"))

    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")

    testCompile(project(":debbie-tomcat"))
}