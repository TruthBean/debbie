dependencies {
    api(project(":debbie-core"))

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2-boot:$loggerVersion")

    // val mysqlVersion: String by project
    // testImplementation("mysql:mysql-connector-java:$mysqlVersion")

    // testCompile(project(":debbie-hikari"))
    testImplementation(project(":debbie-test"))
    
    val mariadbVersion: String by project
    testImplementation("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")

    // todo: sqlite3 postgresql oracle h2 等
    val sqliteVersion: String by project
    testImplementation("org.xerial:sqlite-jdbc:$sqliteVersion")
}