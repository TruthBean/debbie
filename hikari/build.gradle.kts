dependencies {
    compile(project(":debbie-jdbc"))

    val hikariVersion: String by project
    compile("com.zaxxer:HikariCP:$hikariVersion") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")
    
    val mysqlVersion: String by project
    testImplementation("mysql:mysql-connector-java:$mysqlVersion")
    
    val mariadbVersion: String by project
    testCompile("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")

    testCompile(project(":debbie-test"))
}