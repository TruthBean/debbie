dependencies {
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

    // val slf4jVersion: String by project
    // compileOnly("org.slf4j:slf4j-api:$slf4jVersion")

    compileOnly("org.graalvm.nativeimage:svm:20.2.0")

    testImplementation(project(":debbie-test"))

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2-boot:$loggerVersion")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
}