dependencies {
    compile(project(":debbie-mvc"))

    compileOnly("javax.servlet:javax.servlet-api:4.0.0")
    compileOnly("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:1.2.4")

    compile("org.apache.taglibs:taglibs-standard-spec:1.2.5")
    compile("org.apache.taglibs:taglibs-standard-impl:1.2.5")

    compile("commons-fileupload:commons-fileupload:1.4")
}