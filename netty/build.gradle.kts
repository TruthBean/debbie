dependencies {
    implementation(project(":debbie-mvc"))
    implementation(project(":debbie-server"))

    val nettyVersion: String by project
    // compile("io.netty:netty-all:$nettyVersion")
    implementation("io.netty:netty-codec-http2:$nettyVersion")
//    implementation("io.netty:netty-bom:$nettyVersion")
//    implementation("io.netty:netty-parent:$nettyVersion")

    testImplementation(project(":debbie-test"))
    
    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}