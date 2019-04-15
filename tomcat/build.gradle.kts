dependencies {
    compile(project(":debbie-servlet"))
    compile(project(":debbie-boot"))

    compile("org.apache.tomcat.embed:tomcat-embed-core:9.0.19")
    compile("org.apache.tomcat.embed:tomcat-embed-jasper:9.0.19")
    compile("org.apache.tomcat.embed:tomcat-embed-el:9.0.19")

    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
}