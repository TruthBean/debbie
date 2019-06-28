dependencies {
    compile(project(":debbie-core"))

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    val mysqlVersion: String by project
    // testImplementation("mysql:mysql-connector-java:$mysqlVersion")

    testCompile(project(":debbie-hikari"))
    testCompile(project(":debbie-undertow"))
    
    val mariadbVersion: String by project
    testCompile("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")
}