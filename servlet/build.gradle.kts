dependencies {
    implementation(project(":debbie-mvc"))

    val servletVersion: String by project
    compileOnly("jakarta.servlet:jakarta.servlet-api:$servletVersion")

    val jstlVersion: String by project
    compileOnly("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:$jstlVersion")

    implementation("org.apache.taglibs:taglibs-standard-jstlel:$jstlVersion")

    val fileuploadVersion: String by project
    implementation("commons-fileupload:commons-fileupload:$fileuploadVersion") {
        exclude(group = "javax.servlet", module = "servlet-api")
        exclude(group = "portlet-api", module = "portlet-api")
    }

    testImplementation(project(":debbie-test"))

    val log4j2Version: String by project
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    testImplementation("jakarta.servlet:jakarta.servlet-api:$servletVersion")
    testImplementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:$jstlVersion")

}