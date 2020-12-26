dependencies {
    api(project(":debbie-core"))

    testImplementation(project(":debbie-test"))

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:log4j2-boot:$loggerVersion")

}