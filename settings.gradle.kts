rootProject.name = "debbie"

val modules =
        arrayOf("core",
                "jdbc", "mvc", "rmi",
                "servlet", "httpclient", "hikari",
                "server", "tomcat", "undertow", "netty", "se", "aio",
                "dependencies",
                "test"
        )

modules.forEach { dir ->
    include(dir)
    findProject(":$dir")?.apply {
        name = "debbie-$dir"
        projectDir = File(dir)
        buildFileName = "build.gradle.kts"
    }
}