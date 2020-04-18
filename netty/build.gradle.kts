dependencies {
    api(project(":debbie-mvc"))
    api(project(":debbie-server"))

    val nettyVersion: String by project
    // compile("io.netty:netty-all:$nettyVersion")
    api("io.netty:netty-codec-http2:$nettyVersion")
    api("io.netty:netty-transport-native-epoll:$nettyVersion")
    api("io.netty:netty-transport-native-kqueue:$nettyVersion")
    api("io.netty:netty-resolver-dns:$nettyVersion")
    api("io.netty:netty-handler-proxy:$nettyVersion")
    api("io.netty:netty-codec-mqtt:$nettyVersion")
    api("io.netty:netty-transport-rxtx:$nettyVersion")
    api("io.netty:netty-codec-stomp:$nettyVersion")
    api("io.netty:netty-transport-sctp:$nettyVersion")
    api("io.netty:netty-transport-udt:$nettyVersion")
    api("io.netty:netty-codec-haproxy:$nettyVersion")
    api("io.netty:netty-codec-redis:$nettyVersion")
    api("io.netty:netty-codec-memcache:$nettyVersion")
    api("io.netty:netty-codec-smtp:$nettyVersion")
//    implementation("io.netty:netty-bom:$nettyVersion")
//    implementation("io.netty:netty-parent:$nettyVersion")

    testImplementation(project(":debbie-test"))
    
    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}