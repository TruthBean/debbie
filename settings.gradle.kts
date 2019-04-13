rootProject.name = "debbie"

val modules =
        arrayOf("core",
                "jdbc", "mvc",
                "servlet", "httpclient",
                "boot",
                "tomcat", "undertow",
                "example")

modules.forEach { dir ->
    include(dir)
    findProject(":$dir")?.apply {
        name = "debbie-$dir"
        projectDir = File(dir)
        buildFileName = "build.gradle.kts"
    }
}