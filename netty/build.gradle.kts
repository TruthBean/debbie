dependencies {
    api(project(":debbie-server"))

    val nettyVersion: String by project
    api("io.netty:netty-codec-http2:$nettyVersion")

    testImplementation(project(":debbie-test"))

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2-boot:$loggerVersion")
}