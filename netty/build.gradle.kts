dependencies {
    implementation(project(":debbie-mvc"))
    implementation(project(":debbie-server"))

    val nettyVersion: String by project
    // compile("io.netty:netty-all:$nettyVersion")
    implementation("io.netty:netty-codec-http2:$nettyVersion")
    implementation("io.netty:netty-transport-native-epoll:$nettyVersion")
    implementation("io.netty:netty-transport-native-kqueue:$nettyVersion")
    implementation("io.netty:netty-resolver-dns:$nettyVersion")
    implementation("io.netty:netty-handler-proxy:$nettyVersion")
    implementation("io.netty:netty-codec-mqtt:$nettyVersion")
    implementation("io.netty:netty-transport-rxtx:$nettyVersion")
    implementation("io.netty:netty-codec-stomp:$nettyVersion")
    implementation("io.netty:netty-transport-sctp:$nettyVersion")
    implementation("io.netty:netty-transport-udt:$nettyVersion")
    implementation("io.netty:netty-codec-haproxy:$nettyVersion")
    implementation("io.netty:netty-codec-redis:$nettyVersion")
    implementation("io.netty:netty-codec-memcache:$nettyVersion")
    implementation("io.netty:netty-codec-smtp:$nettyVersion")
//    implementation("io.netty:netty-bom:$nettyVersion")
//    implementation("io.netty:netty-parent:$nettyVersion")

    testImplementation(project(":debbie-test"))
    
    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}