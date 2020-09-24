dependencies {
    api(project(":debbie-core"))

    val loggerVersion: String by project
    api("com.truthbean.logger:slf4j-boot:$loggerVersion")
}