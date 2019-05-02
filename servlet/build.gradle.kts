dependencies {
    compile(project(":debbie-mvc"))

    compileOnly("jakarta.servlet:jakarta.servlet-api:4.0.2")
    compileOnly("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:1.2.4")

    compile("org.glassfish.web:javax.servlet.jsp.jstl:1.2.4") {
        exclude(group = "javax.servlet", module = "servlet-api")
        exclude(group = "javax.servlet.jsp", module = "jsp-api")
        exclude(group = "javax.servlet.jsp.jstl", module = "jstl-api")
    }

    compile("commons-fileupload:commons-fileupload:1.4") {
        exclude(group = "javax.servlet", module = "servlet-api")
        exclude(group = "portlet-api", module = "portlet-api")
    }
}