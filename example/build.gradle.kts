// apply(plugin = "war")

dependencies {
    compile(project(":debbie-tomcat"))
    // "compile"(project(":debbie-undertow"))
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")

    // testImplementation("org.springframework.boot:spring-boot-starter-web:2.1.4.RELEASE")
}