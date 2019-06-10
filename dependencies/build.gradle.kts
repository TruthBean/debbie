plugins {
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}
dependencyManagement {
    dependencies {
        // core-main
        val asmVersion: String by project
        dependency("org.ow2.asm:asm:$asmVersion")
        val jacksonVersion: String by project
        dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
        val slf4jVersion: String by project
        dependency("org.slf4j:slf4j-api:$slf4jVersion")

        // core-test
        val jupiterVersion: String by project
        dependency("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
        dependency("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
        dependency("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")

        val log4j2Version: String by project
        dependency("org.apache.logging.log4j:log4j-slf4j-impl:$log4j2Version")

        // jdbc-test
        val mysqlVersion: String by project
        dependency("mysql:mysql-connector-java:$mysqlVersion")
        val mariadbVersion: String by project
        dependency("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")

        // hikari
        val hikariVersion: String by project
        dependency("com.zaxxer:HikariCP:$hikariVersion")

        // servlet-main
        val servletVersion: String by project
        dependency("jakarta.servlet:jakarta.servlet-api:$servletVersion")
        val jstlVersion: String by project
        dependency("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:$jstlVersion")
        dependency("org.glassfish.web:javax.servlet.jsp.jstl:$jstlVersion") {
            exclude("javax.servlet:servlet-api")
            exclude("javax.servlet.jsp:jsp-api")
            exclude("javax.servlet.jsp.jstl:jstl-api")
        }

        val fileuploadVersion: String by project
        dependency("commons-fileupload:commons-fileupload:$fileuploadVersion") {
            exclude("javax.servlet:servlet-api")
            exclude("portlet-api:portlet-api")
        }

        // tomcat
        val tomcatVersion: String by project
        dependency("org.apache.tomcat.embed:tomcat-embed-core:$tomcatVersion")
        dependency("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatVersion")
        dependency("org.apache.tomcat.embed:tomcat-embed-el:$tomcatVersion")

        // undertow
        val undertowVersion: String by project
        dependency("io.undertow:undertow-core:$undertowVersion")

        // netty
        val nettyVersion: String by project
        dependency("io.netty:netty-all:$nettyVersion")

        // debbie
        val projectVersion: String by project
        dependency("com.truthbean.debbie:debbie-core:$projectVersion")
        dependency("com.truthbean.debbie:debbie-jdbc:$projectVersion")
        dependency("com.truthbean.debbie:debbie-mvc:$projectVersion")
        dependency("com.truthbean.debbie:debbie-servlet:$projectVersion")
        dependency("com.truthbean.debbie:debbie-httpclient:$projectVersion")
        dependency("com.truthbean.debbie:debbie-hikari:$projectVersion")
        dependency("com.truthbean.debbie:debbie-server:$projectVersion")
        dependency("com.truthbean.debbie:debbie-tomcat:$projectVersion")
        dependency("com.truthbean.debbie:debbie-undertow:$projectVersion")
        dependency("com.truthbean.debbie:debbie-netty:$projectVersion")
        dependency("com.truthbean.debbie:debbie-mybatis:$projectVersion")
    }
}