dependencies {
    api(project(":debbie-core"))

    val asmVersion: String by project
    testImplementation("org.ow2.asm:asm:$asmVersion")

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2:$loggerVersion")

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    // val mysqlVersion: String by project
    // testImplementation("mysql:mysql-connector-java:$mysqlVersion")

    // testCompile(project(":debbie-hikari"))
    testImplementation(project(":debbie-test"))
    
    // val mariadbVersion: String by project
    // testImplementation("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")

    // todo: sqlite3 postgresql oracle h2 等
    val sqliteVersion: String by project
    testImplementation("org.xerial:sqlite-jdbc:$sqliteVersion")
}