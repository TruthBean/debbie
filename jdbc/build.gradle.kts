dependencies {
    compile(project(":debbie-core"))
    
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8")
    // testImplementation("mysql:mysql-connector-java:5.1.47")
    testCompile("org.mariadb.jdbc:mariadb-java-client:2.4.1")
}