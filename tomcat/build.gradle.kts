dependencies {
    implementation(project(":debbie-servlet"))
    implementation(project(":debbie-server"))

    val tomcatVersion: String by project
    implementation("org.apache.tomcat.embed:tomcat-embed-core:$tomcatVersion")
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatVersion")
    implementation("org.apache.tomcat.embed:tomcat-embed-el:$tomcatVersion")

    testImplementation(project(":debbie-test"))
    
    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}