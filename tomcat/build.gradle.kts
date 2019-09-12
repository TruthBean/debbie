dependencies {
    compile(project(":debbie-servlet"))
    compile(project(":debbie-server"))

    val tomcatVersion: String by project
    compile("org.apache.tomcat.embed:tomcat-embed-core:$tomcatVersion")
    compile("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatVersion")
    compile("org.apache.tomcat.embed:tomcat-embed-el:$tomcatVersion")

    testCompile(project(":debbie-test"))
    
    val log4j2Version: String by project
    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}