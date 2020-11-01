dependencies {
    implementation(project(":debbie-core"))

    val jupiterVersion: String by project
    api("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    api("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
    api("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")

    val loggerVersion: String by project
    testImplementation("com.truthbean.logger:slf4j-boot:$loggerVersion")
}