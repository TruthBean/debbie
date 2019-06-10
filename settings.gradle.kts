rootProject.name = "debbie"

val modules =
        arrayOf("core",
                "jdbc", "mvc",
                "servlet", "httpclient", "hikari",
                "server", "tomcat", "undertow", "netty",
                "dependencies"
        )

modules.forEach { dir ->
    include(dir)
    findProject(":$dir")?.apply {
        name = "debbie-$dir"
        projectDir = File(dir)
        buildFileName = "build.gradle.kts"
    }
}