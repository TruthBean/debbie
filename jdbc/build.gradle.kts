dependencies {
    compile(project(":debbie-core"))
    
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
    testImplementation("mysql:mysql-connector-java:5.1.47")
}