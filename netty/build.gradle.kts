dependencies {
    compile(project(":debbie-mvc"))
    compile(project(":debbie-boot"))

    val nettyVersion: String by project
    compile("io.netty:netty-all:$nettyVersion")
}