dependencies {
    compile(project(":debbie-mvc"))

    val servletVersion: String by project
    compileOnly("jakarta.servlet:jakarta.servlet-api:$servletVersion")

    val jstlVersion: String by project
    compileOnly("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:$jstlVersion")

    compile("org.glassfish.web:javax.servlet.jsp.jstl:$jstlVersion") {
        exclude(group = "javax.servlet", module = "servlet-api")
        exclude(group = "javax.servlet.jsp", module = "jsp-api")
        exclude(group = "javax.servlet.jsp.jstl", module = "jstl-api")
    }

    val fileuploadVersion: String by project
    compile("commons-fileupload:commons-fileupload:$fileuploadVersion") {
        exclude(group = "javax.servlet", module = "servlet-api")
        exclude(group = "portlet-api", module = "portlet-api")
    }

    testCompile(project(":debbie-test"))

    val log4j2Version: String by project
    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

    val jacksonVersion: String by project
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    testCompile("jakarta.servlet:jakarta.servlet-api:$servletVersion")
    testCompile("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:$jstlVersion")

}