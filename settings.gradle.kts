rootProject.name = "debbie"

val modules =
        arrayOf("core",
                "jdbc", "mvc",
                "servlet", "httpclient", "zaxxer",
                "boot",
                "tomcat", "undertow", "netty",
                "example"
        )

modules.forEach { dir ->
    include(dir)
    findProject(":$dir")?.apply {
        name = "debbie-$dir"
        projectDir = File(dir)
        buildFileName = "build.gradle.kts"
    }
}