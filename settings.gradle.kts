rootProject.name = "debbie"

val modules =
        arrayOf("core",
                "httpclient", "jdbc", "mvc",
                "servlet",
                "boot",
                "example")

modules.forEach { dir ->
    include(dir)
    findProject(":$dir")?.apply {
        name = "debbie-$dir"
        projectDir = File(dir)
        buildFileName = "build.gradle.kts"
    }
}