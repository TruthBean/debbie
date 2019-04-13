dependencies {
    compile(project(":debbie-mvc"))
    compile(project(":debbie-boot"))

    compile("io.undertow:undertow-core:2.0.20.Final")
    compile("io.undertow:undertow-servlet:2.0.20.Final")

    testCompile("org.apache.logging.log4j:log4j-slf4j-impl:2.11.2")
}