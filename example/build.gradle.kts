// apply(plugin = "war")

dependencies {
    // compile(project(":debbie-undertow"))
    compile(project(":debbie-tomcat"))
    // compile(project(":debbie-netty"))

    compile(project(":debbie-httpclient"))

    compile(project(":debbie-hikari"))

    val jacksonVersion: String by project
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    val log4j2Version: String by project
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val lang3Version: String by project
    compile("org.apache.commons:commons-lang3:$lang3Version")

    val mysqlVersion: String by project
    implementation("mysql:mysql-connector-java:$mysqlVersion")
}