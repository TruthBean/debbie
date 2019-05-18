dependencies {
    compile(project(":debbie-jdbc"))

    compile("com.zaxxer:HikariCP:3.3.1")

    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
    testImplementation("mysql:mysql-connector-java:8.0.16")
    testCompile("org.mariadb.jdbc:mariadb-java-client:2.4.1")
}