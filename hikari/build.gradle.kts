dependencies {
    compile(project(":debbie-jdbc"))

    val hikariVersion: String by project
    compile("com.zaxxer:HikariCP:$hikariVersion")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")
    
    val mysqlVersion: String by project
    testImplementation("mysql:mysql-connector-java:$mysqlVersion")
    
    val mariadbVersion: String by project
    testCompile("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")
}